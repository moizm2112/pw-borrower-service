package com.paywallet.userservice.user.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paywallet.userservice.user.constant.AppConstants;
import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.exception.FineractAPIException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.SMSAndEmailNotificationException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.LenderConfigInfo;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.UpdateCustomerCredentialsModel;
import com.paywallet.userservice.user.model.UpdateCustomerCredentialsResponse;
import com.paywallet.userservice.user.model.UpdateCustomerDetailsResponseDTO;
import com.paywallet.userservice.user.model.UpdateCustomerEmailIdDTO;
import com.paywallet.userservice.user.model.UpdateCustomerMobileNoDTO;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationResponseWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationResponseWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationResponseWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationResponseWrapperModel;
import com.paywallet.userservice.user.repository.CustomerRepository;
import com.paywallet.userservice.user.util.CommonUtil;
import com.paywallet.userservice.user.util.KafkaPublisherUtil;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerWrapperAPIService {

	@Autowired
	CustomerService customerService;
	
	@Value("${identifyProviderService.eureka.uri}")
	private String identifyProviderServiceUri;
	
	@Autowired
    CustomerServiceHelper customerServiceHelper;
    
    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    CustomerFieldValidator customerFieldValidator;
    
    @Autowired
    CustomerRepository customerRepository;
	
    @Autowired
    KafkaPublisherUtil kafkaPublisherUtil;

	@Autowired
	CommonUtil commonUtil;
	

	public UpdateCustomerCredentialsResponse updateCustomerCredentials(UpdateCustomerCredentialsModel customerCredentialsModel, String requestId) 
			throws CustomerNotFoundException, RequestIdNotFoundException {
		Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
		UpdateCustomerCredentialsResponse updateCustomerCredentialsResponse =  new UpdateCustomerCredentialsResponse();
		UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponse =  null;
		try {
			
			if(StringUtils.isBlank(customerCredentialsModel.getNewEmailId()) && StringUtils.isBlank(customerCredentialsModel.getNewCellPhone())) {
				log.error("Updating customer credentials (New EmailId or CellPhone) is not found");
				throw new GeneralCustomException("ERROR", "Updating customer credentials (New EmailId or CellPhone) is not found");
			}
			
			if(StringUtils.isNotBlank(customerCredentialsModel.getNewEmailId())) {
				List<String> lsError = customerFieldValidator.validateEmailId(customerCredentialsModel.getNewEmailId(), customerRepository, customerCredentialsModel.getCellPhone());
				if(lsError.size() > 0) {
					mapErrorList.put("New EmailId : ", lsError);
				}
				else {
					UpdateCustomerEmailIdDTO updateCustomerEmailIdDTO = setDTOForEmailUpdate(customerCredentialsModel);
					updateCustomerDetailsResponse = customerService.updateCustomerEmailId(updateCustomerEmailIdDTO, requestId);
					setUpdateCustomerCredentialsEmailResponse(updateCustomerDetailsResponse, updateCustomerCredentialsResponse);
				}
			}
			else {
				updateCustomerCredentialsResponse.setEmailId(StringUtils.EMPTY);
				updateCustomerCredentialsResponse.setEmailIdVerified(StringUtils.EMPTY);
			}
			
			if(StringUtils.isNotBlank(customerCredentialsModel.getNewCellPhone())) {
				List<String> lsError = customerFieldValidator.validateMobileNo(customerCredentialsModel.getNewCellPhone());
				if(lsError.size() > 0) {
					mapErrorList.put("New CellPhone : ", lsError);
				}
				else {
					UpdateCustomerMobileNoDTO updateCustomerMobileNoDTO = setDTOForMobileNoUpdate(customerCredentialsModel);
					updateCustomerDetailsResponse = customerService.updateCustomerMobileNo(updateCustomerMobileNoDTO, requestId);
					setUpdateCustomerCredentialsMobileResponse(updateCustomerDetailsResponse, updateCustomerCredentialsResponse);
				}
			}
			else {
				updateCustomerCredentialsResponse.setCellPhone(updateCustomerDetailsResponse.getCellPhone());
				updateCustomerCredentialsResponse.setCellPhoneVerified(StringUtils.EMPTY);
			}
			updateCustomerCredentialsResponse.setRequestId(requestId);
			
			if(mapErrorList.size() > 0) {
				   ObjectMapper objectMapper = new ObjectMapper();
				   String json = "";
			        try {
			            json = objectMapper.writeValueAsString(mapErrorList);
			            log.error("Exception while updating customer credentials  - " + json);
			        } catch (JsonProcessingException e) {
						Sentry.captureException(e);
			        	throw new GeneralCustomException("ERROR", "Exception while updating customer credentials - " + mapErrorList);
			        }
				   throw new GeneralCustomException("ERROR", "Exception while updating customer credentials  - " + json);
			   }
		}
		catch(CustomerNotFoundException | RequestIdNotFoundException | FineractAPIException | GeneralCustomException e) {
			Sentry.captureException(e);
			log.error("Exception occured while updating customer credentials " + e.getMessage());
			throw e;
		}
		catch(Exception e) {
			Sentry.captureException(e);
			log.error("Exception occured while updating customer credentials " + e.getMessage());
    		throw new GeneralCustomException("ERROR", e.getMessage());
		}
		return updateCustomerCredentialsResponse;
	}
	
	public void setUpdateCustomerCredentialsEmailResponse(UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponse, UpdateCustomerCredentialsResponse updateCustomerCredentialsResponse) {
		updateCustomerCredentialsResponse.setEmailId(updateCustomerDetailsResponse.getEmailId());
		
		/* NEED TO UPDATE IT WITH ACTUAL VALUE AFTER UNDERSTANDING THE REQUIREMENTS */
		updateCustomerCredentialsResponse.setEmailIdVerified(StringUtils.EMPTY);
		
	}
	
	public void setUpdateCustomerCredentialsMobileResponse(UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponse, UpdateCustomerCredentialsResponse updateCustomerCredentialsResponse) {
		updateCustomerCredentialsResponse.setCellPhone(updateCustomerDetailsResponse.getCellPhone());
		
		/* NEED TO UPDATE IT WITH ACTUAL VALUE AFTER UNDERSTANDING THE REQUIREMENTS */
		updateCustomerCredentialsResponse.setCellPhoneVerified(StringUtils.EMPTY);
	}
	
	public UpdateCustomerEmailIdDTO setDTOForEmailUpdate(UpdateCustomerCredentialsModel customerCredentialsModel) {
		
		UpdateCustomerEmailIdDTO updateCustomerEmailId = new UpdateCustomerEmailIdDTO();
		
		updateCustomerEmailId.setCellPhone(customerCredentialsModel.getCellPhone());
		updateCustomerEmailId.setEmailId(customerCredentialsModel.getEmailId());
		updateCustomerEmailId.setNewEmailId(customerCredentialsModel.getNewEmailId());
		
		return updateCustomerEmailId;
	}
	
	public UpdateCustomerMobileNoDTO setDTOForMobileNoUpdate(UpdateCustomerCredentialsModel customerCredentialsModel) {
		
		UpdateCustomerMobileNoDTO updateCustomerMobileNoDTO = new UpdateCustomerMobileNoDTO();
		
		updateCustomerMobileNoDTO.setCellPhone(customerCredentialsModel.getCellPhone());
		updateCustomerMobileNoDTO.setNewCellPhone(customerCredentialsModel.getNewCellPhone());
		
		return updateCustomerMobileNoDTO;
	}
	
	public ResponseEntity<Object> prepareUpdateResponse(Object updateCustomerCredentialsModel, String message, int status, String path) {
	       
	   Map<String, Object> body = new LinkedHashMap<>();
	   body.put("data", updateCustomerCredentialsModel);
	   body.put("message", message);
	   body.put("status", status);
	   body.put("timestamp", new Date());
	   body.put("path", path);
	   return new ResponseEntity<>(body, HttpStatus.OK);
    }
	
	
	public void setCustomerRequest(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel, CreateCustomerRequest customer) {
		if(depositAllocationRequestWrapperModel != null) {
			
			if(depositAllocationRequestWrapperModel.getNumberOfInstallments() == null)
				depositAllocationRequestWrapperModel.setNumberOfInstallments(0);
	    	if(depositAllocationRequestWrapperModel.getInstallmentAmount() ==null)
	    		depositAllocationRequestWrapperModel.setInstallmentAmount(0);
	    	if(depositAllocationRequestWrapperModel.getLoanAmount() ==null)
	    		depositAllocationRequestWrapperModel.setLoanAmount(0);
			
			customer.setFirstName(depositAllocationRequestWrapperModel.getFirstName());
			customer.setLastName(depositAllocationRequestWrapperModel.getLastName());
			customer.setCellPhone(depositAllocationRequestWrapperModel.getCellPhone());
			customer.setEmailId(depositAllocationRequestWrapperModel.getEmailId());
			customer.setFirstDateOfPayment(depositAllocationRequestWrapperModel.getFirstDateOfPayment());
			customer.setRepaymentFrequency(depositAllocationRequestWrapperModel.getRepaymentFrequency());
			customer.setNumberOfInstallments(depositAllocationRequestWrapperModel.getNumberOfInstallments());
			customer.setInstallmentAmount(depositAllocationRequestWrapperModel.getInstallmentAmount());
			customer.setCallbackURLs(depositAllocationRequestWrapperModel.getCallbackURLs());
			customer.setZip(StringUtils.EMPTY);
			customer.setState(StringUtils.EMPTY);
			customer.setAddressLine1(StringUtils.EMPTY);
			customer.setAddressLine2(StringUtils.EMPTY);
			customer.setMiddleName(StringUtils.EMPTY);
			customer.setCity(StringUtils.EMPTY);
			customer.setLast4TIN(StringUtils.EMPTY);
			customer.setDateOfBirth(StringUtils.EMPTY);
		}
	}
	
	public void setCustomerRequestForEmployment(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel, CreateCustomerRequest customer) {
		if(employmentVerificationRequestWrapperModel != null) {
			customer.setFirstName(employmentVerificationRequestWrapperModel.getFirstName());
			customer.setLastName(employmentVerificationRequestWrapperModel.getLastName());
			customer.setCellPhone(employmentVerificationRequestWrapperModel.getCellPhone());
			customer.setEmailId(employmentVerificationRequestWrapperModel.getEmailId());
			/*if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getEmploymentCallbackUrl())) {
				CallbackURL callbackURL = new CallbackURL();
				List<String> employmentCallbackUrls =  new ArrayList<String>();
				employmentCallbackUrls.add(employmentVerificationRequestWrapperModel.getEmploymentCallbackUrl());
				callbackURL.setEmploymentCallbackUrls(employmentCallbackUrls);
				customer.setCallbackURLs(callbackURL);
			}*/
			customer.setCallbackURLs(employmentVerificationRequestWrapperModel.getCallbackURLs());
			customer.setFirstDateOfPayment(StringUtils.EMPTY);
			customer.setRepaymentFrequency(StringUtils.EMPTY);
			customer.setNumberOfInstallments(0);
			customer.setInstallmentAmount(0);
			customer.setZip(StringUtils.EMPTY);
			customer.setState(StringUtils.EMPTY);
			customer.setAddressLine1(StringUtils.EMPTY);
			customer.setAddressLine2(StringUtils.EMPTY);
			customer.setMiddleName(StringUtils.EMPTY);
			customer.setCity(StringUtils.EMPTY);
			customer.setLast4TIN(StringUtils.EMPTY);
			customer.setDateOfBirth(StringUtils.EMPTY);
		}
	}
	
	public void setCustomerRequestForIdentity(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel, CreateCustomerRequest customer) {
		
		if(identityVerificationRequestWrapperModel != null) {
			customer.setFirstName(identityVerificationRequestWrapperModel.getFirstName());
			customer.setLastName(identityVerificationRequestWrapperModel.getLastName());
			customer.setCellPhone(identityVerificationRequestWrapperModel.getCellPhone());
			customer.setEmailId(identityVerificationRequestWrapperModel.getEmailId());
			/*if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getIdentityCallbackUrl())) {
				CallbackURL callbackURL = new CallbackURL();
				List<String> identityCallbackUrls =  new ArrayList<String>();
				identityCallbackUrls.add(identityVerificationRequestWrapperModel.getIdentityCallbackUrl());
				callbackURL.setIdentityCallbackUrls(identityCallbackUrls);
				customer.setCallbackURLs(callbackURL);
			}*/
			customer.setCallbackURLs(identityVerificationRequestWrapperModel.getCallbackURLs());
			customer.setFirstDateOfPayment(StringUtils.EMPTY);
			customer.setRepaymentFrequency(StringUtils.EMPTY);
			customer.setNumberOfInstallments(0);
			customer.setInstallmentAmount(0);
			customer.setZip(identityVerificationRequestWrapperModel.getZip());
			customer.setState(identityVerificationRequestWrapperModel.getState());
			customer.setAddressLine1(identityVerificationRequestWrapperModel.getAddressLine1());
			customer.setAddressLine2(identityVerificationRequestWrapperModel.getAddressLine2());
			customer.setMiddleName(StringUtils.EMPTY);
			customer.setCity(identityVerificationRequestWrapperModel.getCity());
			customer.setLast4TIN(identityVerificationRequestWrapperModel.getLast4TIN());
			customer.setDateOfBirth(identityVerificationRequestWrapperModel.getDateOfBirth());
		}
	}
	
	public void setCustomerRequestForIncome(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel, CreateCustomerRequest customer) {
		
		if(incomeVerificationRequestWrapperModel != null) {
			customer.setFirstName(incomeVerificationRequestWrapperModel.getFirstName());
			customer.setLastName(incomeVerificationRequestWrapperModel.getLastName());
			customer.setCellPhone(incomeVerificationRequestWrapperModel.getCellPhone());
			customer.setEmailId(incomeVerificationRequestWrapperModel.getEmailId());
			/*if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getIncomeCallbackUrl())) {
				CallbackURL callbackURL = new CallbackURL();
				List<String> incomeCallbackUrls =  new ArrayList<String>();
				incomeCallbackUrls.add(incomeVerificationRequestWrapperModel.getIncomeCallbackUrl());
				callbackURL.setIncomeCallbackUrls(incomeCallbackUrls);
				customer.setCallbackURLs(callbackURL);
			}*/
			customer.setCallbackURLs(incomeVerificationRequestWrapperModel.getCallbackURLs());
			customer.setFirstDateOfPayment(StringUtils.EMPTY);
			customer.setRepaymentFrequency(StringUtils.EMPTY);
			customer.setNumberOfInstallments(0);
			customer.setInstallmentAmount(0);
			customer.setZip(StringUtils.EMPTY);
			customer.setState(StringUtils.EMPTY);
			customer.setAddressLine1(StringUtils.EMPTY);
			customer.setAddressLine2(StringUtils.EMPTY);
			customer.setMiddleName(StringUtils.EMPTY);
			customer.setCity(StringUtils.EMPTY);
			customer.setLast4TIN(StringUtils.EMPTY);
			customer.setDateOfBirth(StringUtils.EMPTY);
		}
	}
	
	public DepositAllocationResponseWrapperModel initiateDepositAllocation(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel, String requestId)
			throws CreateCustomerException, GeneralCustomException, ServiceNotAvailableException, RequestIdNotFoundException, SMSAndEmailNotificationException {

		CreateCustomerRequest customer = new CreateCustomerRequest();
		setCustomerRequest(depositAllocationRequestWrapperModel, customer);
		
		CustomerDetails customerDetails = customerService.createCustomer(customer, requestId, depositAllocationRequestWrapperModel, FlowTypeEnum.DEPOSIT_ALLOCATION);
		DepositAllocationResponseWrapperModel depositAllocationResponse = setDepositAllocationResponse(customerDetails, depositAllocationRequestWrapperModel);
		return depositAllocationResponse;
	}

	public EmploymentVerificationResponseWrapperModel initiateEmploymentVerification(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel,
			String requestId) throws CreateCustomerException, GeneralCustomException, ServiceNotAvailableException, RequestIdNotFoundException, 
				SMSAndEmailNotificationException {
		
		CreateCustomerRequest customer = new CreateCustomerRequest();
		setCustomerRequestForEmployment(employmentVerificationRequestWrapperModel, customer);
		
		CustomerDetails customerDetails = customerService.createCustomer(customer, requestId, employmentVerificationRequestWrapperModel, FlowTypeEnum.EMPLOYMENT_VERIFICATION);
		EmploymentVerificationResponseWrapperModel employmentVerificationResponse = setEmploymentVerificationResponse(customerDetails, employmentVerificationRequestWrapperModel);
		return employmentVerificationResponse;
	}
	
	public IdentityVerificationResponseWrapperModel initiateIdentityVerification(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel,
			String requestId) throws CreateCustomerException, GeneralCustomException, ServiceNotAvailableException, RequestIdNotFoundException, 
				SMSAndEmailNotificationException {
		
		CreateCustomerRequest customer = new CreateCustomerRequest();
		setCustomerRequestForIdentity(identityVerificationRequestWrapperModel, customer);
		
		CustomerDetails customerDetails = customerService.createCustomer(customer, requestId, identityVerificationRequestWrapperModel, FlowTypeEnum.IDENTITY_VERIFICATION);
		IdentityVerificationResponseWrapperModel identityVerificationResponse = setIdentityVerificationResponse(customerDetails, identityVerificationRequestWrapperModel);
		return identityVerificationResponse;
	}
	
	public IncomeVerificationResponseWrapperModel initiateIncomeVerification(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel,
			String requestId) throws CreateCustomerException, GeneralCustomException, ServiceNotAvailableException, RequestIdNotFoundException, 
				SMSAndEmailNotificationException {
		
		CreateCustomerRequest customer = new CreateCustomerRequest();
		setCustomerRequestForIncome(incomeVerificationRequestWrapperModel, customer);
		
		CustomerDetails customerDetails = customerService.createCustomer(customer, requestId, incomeVerificationRequestWrapperModel, FlowTypeEnum.INCOME_VERIFICATION);
		IncomeVerificationResponseWrapperModel incomeVerificationResponse = setIncomeVerificationResponse(customerDetails, incomeVerificationRequestWrapperModel);
		return incomeVerificationResponse;
	}
	
	public DepositAllocationResponseWrapperModel setDepositAllocationResponse(CustomerDetails customerDetails, DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel) {
		DepositAllocationResponseWrapperModel depositAllocationResponseModel = new DepositAllocationResponseWrapperModel();
		depositAllocationResponseModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		depositAllocationResponseModel.setCellPhone(customerDetails.getPersonalProfile().getCellPhone());
		depositAllocationResponseModel.setVirtualAccountNumber(customerDetails.getVirtualAccount());
		depositAllocationResponseModel.setVirtualAccountABANumber(customerDetails.getAccountABANumber());
		depositAllocationResponseModel.setVirtualAccountId(customerDetails.getVirtualAccountId());
		depositAllocationResponseModel.setNumberOfInstallments(customerDetails.getNumberOfInstallments());
		if(depositAllocationRequestWrapperModel.getInstallmentAmount() > 0)
			depositAllocationResponseModel.setInstallmentAmount(commonUtil.getFormattedAmount(customerDetails.getInstallmentAmount()));
		else if(depositAllocationRequestWrapperModel.getLoanAmount() > 0) {
			depositAllocationResponseModel.setInstallmentAmount(commonUtil.getFormattedAmount(customerService.getInstallmentAmount(depositAllocationRequestWrapperModel.getLoanAmount(),
					depositAllocationRequestWrapperModel.getInstallmentAmount(), depositAllocationRequestWrapperModel.getNumberOfInstallments())));
			depositAllocationResponseModel.setLoanAmount(commonUtil.getFormattedAmount(depositAllocationRequestWrapperModel.getLoanAmount()));
		}
		return depositAllocationResponseModel;
	}
	
	public EmploymentVerificationResponseWrapperModel setEmploymentVerificationResponse(CustomerDetails customerDetails, EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel) {
		EmploymentVerificationResponseWrapperModel employmentVerificationResponseModel = new EmploymentVerificationResponseWrapperModel();
		employmentVerificationResponseModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		employmentVerificationResponseModel.setCellPhone(customerDetails.getPersonalProfile().getCellPhone());
		employmentVerificationResponseModel.setLenderName(customerDetails.getLender());
		employmentVerificationResponseModel.setEmployer(customerDetails.getEmployer());
		employmentVerificationResponseModel.setCallbackURLs(employmentVerificationRequestWrapperModel.getCallbackURLs());
		employmentVerificationResponseModel.setFirstName(employmentVerificationRequestWrapperModel.getFirstName());
		employmentVerificationResponseModel.setLastName(employmentVerificationRequestWrapperModel.getLastName());
		return employmentVerificationResponseModel;
	}
	
	public IncomeVerificationResponseWrapperModel setIncomeVerificationResponse(CustomerDetails customerDetails, IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel) {
		IncomeVerificationResponseWrapperModel incomeVerificationResponseModel = new IncomeVerificationResponseWrapperModel();
		incomeVerificationResponseModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		incomeVerificationResponseModel.setCellPhone(customerDetails.getPersonalProfile().getCellPhone());
		incomeVerificationResponseModel.setNumberOfMonthsRequested(incomeVerificationRequestWrapperModel.getNumberOfMonthsRequested());
		incomeVerificationResponseModel.setEmployer(customerDetails.getEmployer());
		incomeVerificationResponseModel.setCallbackURLs(incomeVerificationRequestWrapperModel.getCallbackURLs());
		incomeVerificationResponseModel.setFirstName(incomeVerificationRequestWrapperModel.getFirstName());
		incomeVerificationResponseModel.setLastName(incomeVerificationRequestWrapperModel.getLastName());
		return incomeVerificationResponseModel;
	}
	
	public IdentityVerificationResponseWrapperModel setIdentityVerificationResponse(CustomerDetails customerDetails, IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel) {
		IdentityVerificationResponseWrapperModel identityVerificationResponseWrapperModel = new IdentityVerificationResponseWrapperModel();
		identityVerificationResponseWrapperModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		identityVerificationResponseWrapperModel.setCellPhone(customerDetails.getPersonalProfile().getCellPhone());
		identityVerificationResponseWrapperModel.setLast4TIN(identityVerificationRequestWrapperModel.getLast4TIN());
		identityVerificationResponseWrapperModel.setEmployer(customerDetails.getEmployer());
		identityVerificationResponseWrapperModel.setCallbackURLs(identityVerificationRequestWrapperModel.getCallbackURLs());
		identityVerificationResponseWrapperModel.setFirstName(identityVerificationRequestWrapperModel.getFirstName());
		identityVerificationResponseWrapperModel.setLastName(identityVerificationRequestWrapperModel.getLastName());
		return identityVerificationResponseWrapperModel;
	}
	
	
	
	
	public void validateDepositAllocationRequest(DepositAllocationRequestWrapperModel allocationRequest, String requestId, RequestIdDetails requestIdDetails, LenderConfigInfo lenderConfigInfo){
	   Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
	   try {
		   
		   String lender = requestIdDetails.getClientName();
		   String employerPWId = requestIdDetails.getEmployerPWId();
		   
		   if(StringUtils.isNotBlank(allocationRequest.getFirstName())) {
			   List<String> errorList = customerFieldValidator.validateFirstName(allocationRequest.getFirstName());
			   if(errorList.size() > 0)
				   mapErrorList.put("First Name", errorList);
		   } 
		   if(StringUtils.isNotBlank(allocationRequest.getLastName())) {
			   List<String> errorList = customerFieldValidator.validateLastName(allocationRequest.getLastName());
			   if(errorList.size() > 0)
				   mapErrorList.put("Last Name", errorList);
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getCellPhone()) || StringUtils.isBlank(allocationRequest.getCellPhone())) {
			   List<String> errorList = customerFieldValidator.validateMobileNo(allocationRequest.getCellPhone());
			   if(errorList.size() > 0)
				   mapErrorList.put("CellPhone Number", errorList);
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getEmployerId()) || StringUtils.isBlank(allocationRequest.getEmployerId())) {
			   List<String> errorList = customerFieldValidator.validateEmployerId(allocationRequest.getEmployerId(), employerPWId);
			   if(errorList.size() > 0)
				   mapErrorList.put("EmployerId", errorList);
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getAchPullRequest())) {
			   List<String> errorList = customerFieldValidator.validateACHPullRequest(allocationRequest.getAchPullRequest());
			   if(errorList.size() > 0)
				   mapErrorList.put("ACH Pull Request", errorList);
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getAccountVerificationOverride())) {
			   List<String> errorList = customerFieldValidator.validateAccountValidationOverride(allocationRequest.getAccountVerificationOverride());
			   if(errorList.size() > 0)
				   mapErrorList.put("Account verfication override", errorList);
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getExternalVirtualAccountABANumber())) {
			   List<String> errorList = customerFieldValidator.validateExternalVirtualAccountABANumber(allocationRequest.getExternalVirtualAccountABANumber());
			   if(errorList.size() > 0)
				   mapErrorList.put("External virtual account ABA number", errorList);
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getEmailId()) || StringUtils.isBlank(allocationRequest.getEmailId())) {
			   List<String> errorList = customerFieldValidator.validateEmailId(allocationRequest.getEmailId(), customerRepository, allocationRequest.getCellPhone());
			   if(errorList.size() > 0)
				   mapErrorList.put("EmailId", errorList);
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getLender())) {
			   if(!lender.equalsIgnoreCase(allocationRequest.getLender())) {
				   List<String> errorList = new ArrayList<String>();
				   errorList.add(AppConstants.LENDER_NAME_NO_MATCH);
				   mapErrorList.put("Lender", errorList);
			   }
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getFirstDateOfPayment())) {
			   List<String> errorList = customerFieldValidator.validateFirstDateOfPayment(allocationRequest.getFirstDateOfPayment(), lender);
			   if(errorList.size() > 0)
				   mapErrorList.put("First Date Of Payment", errorList);
		   }
		   if(StringUtils.isNotBlank(allocationRequest.getRepaymentFrequency())) {
			   List<String> errorList = customerFieldValidator.validateRepaymentFrequency(allocationRequest.getRepaymentFrequency());
			   if(errorList.size() > 0)
				   mapErrorList.put("Repayment Frequency", errorList);
		   }
		   if(allocationRequest.getNumberOfInstallments() != null){
			   List<String> errorList = customerFieldValidator.validateTotalNoOfRepayment(allocationRequest.getNumberOfInstallments());
			   if(errorList.size() > 0)
				   mapErrorList.put("Number Of Installments", errorList);
		   }else {
			   if("YES".equalsIgnoreCase(lenderConfigInfo.getInvokeAndPublishDepositAllocation().name())) {
				   List<String> errorList = new ArrayList<String>();
				   if (allocationRequest.getNumberOfInstallments() == null || allocationRequest.getNumberOfInstallments() <= 0) {
					   errorList.add(AppConstants.NUMBEROFINSTALLMENTS_MANDATORY_MESSAGE);
					   mapErrorList.put("Number Of Installments", errorList);
				   }
			   }
			   else if(allocationRequest.getNumberOfInstallments() != null || allocationRequest.getNumberOfInstallments() >= 0) {
				   List<String> errorList = customerFieldValidator.validateTotalNoOfRepayment(allocationRequest.getNumberOfInstallments());
				   if(errorList.size() > 0)
					   mapErrorList.put("Number of installements", errorList);
			   }
		   }
		   
//		   if(allocationRequest.getLoanAmount() == 0) {
//			   if(allocationRequest.getInstallmentAmount() == 0) {
//				   
//			   }
//		   }
		   
		   if(allocationRequest.getLoanAmount() != null && allocationRequest.getLoanAmount() > 0) {
			   if(allocationRequest.getInstallmentAmount() != null && allocationRequest.getInstallmentAmount() > 0) {
				   List<String> errorList = new ArrayList<String>();
				   errorList.add("Provide either loan amount or installment amount to process");
				   mapErrorList.put("Loan Amount/Installment Amount", errorList);
			   }
			   else {
				   List<String> errorList = customerFieldValidator.validateLoanAmount(allocationRequest.getLoanAmount());
				   if(errorList.size() > 0)
					   mapErrorList.put("Loan Amount", errorList);
			   }
		   }
		   else {
			   if(allocationRequest.getInstallmentAmount() == null || allocationRequest.getInstallmentAmount() == 0) {
				   List<String> errorList = new ArrayList<String>();
				   errorList.add("Provide either Loan Amount or Installment Amount, both cannot be empty or zero");
				   mapErrorList.put("Loan Amount and Installment Amount", errorList);
				   
			   }else {
				   if(allocationRequest.getInstallmentAmount() != null && allocationRequest.getInstallmentAmount() > 0) {
					   List<String> errorList = customerFieldValidator.validateInstallmentAmount(allocationRequest.getInstallmentAmount());
					   if(errorList.size() > 0)
						   mapErrorList.put("Installment Amount", errorList);
				   }
			   }
		   }
		   if(mapErrorList.size() > 0) {
			   ObjectMapper objectMapper = new ObjectMapper();
			   String json = "";
		        try {
		            json = objectMapper.writeValueAsString(mapErrorList);
		            log.error("Invalid data in deposit allocation request - " + json);
		        } catch (JsonProcessingException e) {
					Sentry.captureException(e);
		        	throw new GeneralCustomException("ERROR", "Invalid data in deposit allocation request - " + mapErrorList);
		        }
			   throw new GeneralCustomException("ERROR", "Invalid data in deposit allocation request - " + json);
		   }
	   } catch(GeneralCustomException e) {
		   Sentry.captureException(e);
		   throw e;
	   } catch(Exception e) {
		   Sentry.captureException(e);
		   log.error("Exception occured while validating the deposit allocation request");
		   throw e;
	   }
   }
	
	public void validateEmploymentVerificationRequest(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel, 
		String requestId, RequestIdDetails requestIdDetails, LenderConfigInfo lenderConfigInfo){
	   Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
	   try {
		   
		   String lender = requestIdDetails.getClientName();
		   String employerPWId = requestIdDetails.getEmployerPWId();
		   
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getFirstName())) {
			   List<String> errorList = customerFieldValidator.validateFirstName(employmentVerificationRequestWrapperModel.getFirstName());
			   if(errorList.size() > 0)
				   mapErrorList.put("First Name", errorList);
		   } 
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getLastName())) {
			   List<String> errorList = customerFieldValidator.validateLastName(employmentVerificationRequestWrapperModel.getLastName());
			   if(errorList.size() > 0)
				   mapErrorList.put("Last Name", errorList);
		   }
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getCellPhone()) || StringUtils.isBlank(employmentVerificationRequestWrapperModel.getCellPhone())) {
			   List<String> errorList = customerFieldValidator.validateMobileNo(employmentVerificationRequestWrapperModel.getCellPhone());
			   if(errorList.size() > 0)
				   mapErrorList.put("CellPhone Number", errorList);
		   }
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getEmployerId()) || StringUtils.isBlank(employmentVerificationRequestWrapperModel.getEmployerId())) {
			   List<String> errorList = customerFieldValidator.validateEmployerId(employmentVerificationRequestWrapperModel.getEmployerId(), employerPWId);
			   if(errorList.size() > 0)
				   mapErrorList.put("EmployerId", errorList);
		   }
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getEmailId()) || StringUtils.isBlank(employmentVerificationRequestWrapperModel.getEmailId())) {
			   List<String> errorList = customerFieldValidator.validateEmailId(employmentVerificationRequestWrapperModel.getEmailId(), customerRepository, employmentVerificationRequestWrapperModel.getCellPhone());
			   if(errorList.size() > 0)
				   mapErrorList.put("EmailId", errorList);
		   }
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getLender())) {
			   if(!lender.equalsIgnoreCase(employmentVerificationRequestWrapperModel.getLender())) {
				   List<String> errorList = new ArrayList<String>();
				   errorList.add(AppConstants.LENDER_NAME_NO_MATCH);
				   mapErrorList.put("Lender", errorList);
			   }
		   }
		   
		   if(mapErrorList.size() > 0) {
			   ObjectMapper objectMapper = new ObjectMapper();
			   String json = "";
		        try {
		            json = objectMapper.writeValueAsString(mapErrorList);
		            log.error("Invalid data in employment verification request - " + json);
		        } catch (JsonProcessingException e) {
					Sentry.captureException(e);
		        	throw new GeneralCustomException("ERROR", "Invalid data in employment verification request - " + mapErrorList);
		        }
			   throw new GeneralCustomException("ERROR", "Invalid data in employment verification request - " + json);
		   }
	   } catch(GeneralCustomException e) {
		   Sentry.captureException(e);
		   throw e;
	   } catch(Exception e) {
		   Sentry.captureException(e);
		   log.error("Exception occured while validating the deposit allocation request");
		   throw e;
	   }
   }
	
	public void validateMobileFromRequest(String cellPhone, String verificationType){
		   Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
		   try {
			   
			   
			   if(StringUtils.isNotBlank(cellPhone)) {
				   List<String> errorList = customerFieldValidator.validateMobileNo(cellPhone);
				   if(errorList.size() > 0)
					   mapErrorList.put("CellPhone Number", errorList);
			   }
			   if(mapErrorList.size() > 0) {
				   ObjectMapper objectMapper = new ObjectMapper();
				   String json = "";
			        try {
			            json = objectMapper.writeValueAsString(mapErrorList);
			            log.error("Invalid data in the request - " + json);
			        } catch (JsonProcessingException e) {
			        	throw new GeneralCustomException("ERROR", "Invalid data in "+ verificationType + " verification request - " + mapErrorList);
			        }
				   throw new GeneralCustomException("ERROR", "Invalid data in "+ verificationType + "  verification request - " + json);
			   }
		   } catch(GeneralCustomException e) {
			   Sentry.captureException(e);
			   throw e;
		   } catch(Exception e) {
			   Sentry.captureException(e);
			   log.error("Exception occured while validating the deposit allocation request");
			   throw e;
		   }
	   }
	
	public void validateIncomeVerificationRequest(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel, 
			String requestId, RequestIdDetails requestIdDetails, LenderConfigInfo lenderConfigInfo){
		   Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
		   try {
			   
			   String lender = requestIdDetails.getClientName();
			   String employerPWId = requestIdDetails.getEmployerPWId();
			   
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getFirstName())) {
				   List<String> errorList = customerFieldValidator.validateFirstName(incomeVerificationRequestWrapperModel.getFirstName());
				   if(errorList.size() > 0)
					   mapErrorList.put("First Name", errorList);
			   } 
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getLastName())) {
				   List<String> errorList = customerFieldValidator.validateLastName(incomeVerificationRequestWrapperModel.getLastName());
				   if(errorList.size() > 0)
					   mapErrorList.put("Last Name", errorList);
			   }
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getCellPhone()) || StringUtils.isBlank(incomeVerificationRequestWrapperModel.getCellPhone())) {
				   List<String> errorList = customerFieldValidator.validateMobileNo(incomeVerificationRequestWrapperModel.getCellPhone());
				   if(errorList.size() > 0)
					   mapErrorList.put("CellPhone Number", errorList);
			   }
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getEmployerId()) || StringUtils.isBlank(incomeVerificationRequestWrapperModel.getEmployerId())) {
				   List<String> errorList = customerFieldValidator.validateEmployerId(incomeVerificationRequestWrapperModel.getEmployerId(), employerPWId);
				   if(errorList.size() > 0)
					   mapErrorList.put("EmployerId", errorList);
			   }
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getEmailId()) || StringUtils.isBlank(incomeVerificationRequestWrapperModel.getEmailId())) {
				   List<String> errorList = customerFieldValidator.validateEmailId(incomeVerificationRequestWrapperModel.getEmailId(), customerRepository, incomeVerificationRequestWrapperModel.getCellPhone());
				   if(errorList.size() > 0)
					   mapErrorList.put("EmailId", errorList);
			   }
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getLender())) {
				   if(!lender.equalsIgnoreCase(incomeVerificationRequestWrapperModel.getLender())) {
					   List<String> errorList = new ArrayList<String>();
					   errorList.add(AppConstants.LENDER_NAME_NO_MATCH);
					   mapErrorList.put("Lender", errorList);
				   }
			   }
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getNumberOfMonthsRequested())) {
				   List<String> errorList = customerFieldValidator.validateNoOfMonthsRequested(incomeVerificationRequestWrapperModel.getNumberOfMonthsRequested());
				   if(errorList.size() > 0)
					   mapErrorList.put("Number of months requested", errorList);
			   }
			   
			   if(mapErrorList.size() > 0) {
				   ObjectMapper objectMapper = new ObjectMapper();
				   String json = "";
			        try {
			            json = objectMapper.writeValueAsString(mapErrorList);
			            log.error("Invalid data in income verification request - " + json);
			        } catch (JsonProcessingException e) {
						Sentry.captureException(e);
			        	throw new GeneralCustomException("ERROR", "Invalid data in income verification request - " + mapErrorList);
			        }
				   throw new GeneralCustomException("ERROR", "Invalid data in income verification request - " + json);
			   }
		   } catch(GeneralCustomException e) {
			   Sentry.captureException(e);
			   throw e;
		   } catch(Exception e) {
			   Sentry.captureException(e);
			   log.error("Exception occured while validating the income verification request");
			   throw e;
		   }
	   }
	
	public void validateIdentityVerificationRequest(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel,
			String requestId, RequestIdDetails requestIdDetails, LenderConfigInfo lenderConfigInfo){
		   Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
		   try {
			   
			   String lender = requestIdDetails.getClientName();
			   String employerPWId = requestIdDetails.getEmployerPWId();
			   
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getFirstName()) || StringUtils.isBlank(identityVerificationRequestWrapperModel.getFirstName())) 
			   {
				   List<String> errorList = customerFieldValidator.validateFirstName(identityVerificationRequestWrapperModel.getFirstName());
				   if(errorList.size() > 0)
					   mapErrorList.put("First Name", errorList);
			   } 
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getLastName()) || StringUtils.isBlank(identityVerificationRequestWrapperModel.getLastName())) {
				   List<String> errorList = customerFieldValidator.validateLastName(identityVerificationRequestWrapperModel.getLastName());
				   if(errorList.size() > 0)
					   mapErrorList.put("Last Name", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getCellPhone()) || StringUtils.isBlank(identityVerificationRequestWrapperModel.getCellPhone())) {
				   List<String> errorList = customerFieldValidator.validateMobileNo(identityVerificationRequestWrapperModel.getCellPhone());
				   if(errorList.size() > 0)
					   mapErrorList.put("CellPhone Number", errorList);
			   }
			   
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getAddressLine1())) {
				   List<String> errorList = customerFieldValidator.validateAddressLine1(identityVerificationRequestWrapperModel.getAddressLine1());
				   if(errorList.size() > 0)
					   mapErrorList.put("Address Line1", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getAddressLine2())) {
				   List<String> errorList = customerFieldValidator.validateAddressLine2(identityVerificationRequestWrapperModel.getAddressLine2());
				   if(errorList.size() > 0)
					   mapErrorList.put("Address Line2", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getCity())) {
				   List<String> errorList = customerFieldValidator.validateCity(identityVerificationRequestWrapperModel.getCity());
				   if(errorList.size() > 0)
					   mapErrorList.put("City", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getState())) {
				   List<String> errorList = customerFieldValidator.validateState(identityVerificationRequestWrapperModel.getState());
				   if(errorList.size() > 0)
					   mapErrorList.put("State", errorList);
			   }
			   if(StringUtils.isNotEmpty(identityVerificationRequestWrapperModel.getZip())) {
				   List<String> errorList = customerFieldValidator.validateZip(identityVerificationRequestWrapperModel.getZip());
				   if(errorList.size() > 0)
					   mapErrorList.put("Zip", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getLast4TIN())) {
				   List<String> errorList = customerFieldValidator.validateLast4TIN(identityVerificationRequestWrapperModel.getLast4TIN());
				   if(errorList.size() > 0)
					   mapErrorList.put("Last 4TIN", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getDateOfBirth())) {
				   List<String> errorList = customerFieldValidator.validateDateOfBirth(identityVerificationRequestWrapperModel.getDateOfBirth());
				   if(errorList.size() > 0)
					   mapErrorList.put("Date Of Birth", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getEmailId()) || StringUtils.isBlank(identityVerificationRequestWrapperModel.getEmailId())) {
				   List<String> errorList = customerFieldValidator.validateEmailId(identityVerificationRequestWrapperModel.getEmailId(), customerRepository, identityVerificationRequestWrapperModel.getCellPhone());
				   if(errorList.size() > 0)
					   mapErrorList.put("Email Id", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getEmployerId()) || StringUtils.isBlank(identityVerificationRequestWrapperModel.getEmployerId())) {
				   List<String> errorList = customerFieldValidator.validateEmployerId(identityVerificationRequestWrapperModel.getEmployerId(), employerPWId);
				   if(errorList.size() > 0)
					   mapErrorList.put("Employer Id", errorList);
			   }
			   
			   if(mapErrorList.size() > 0) {
				   ObjectMapper objectMapper = new ObjectMapper();
				   String json = "";
			        try {
			            json = objectMapper.writeValueAsString(mapErrorList);
			            log.error("Invalid data in identity verification - " + json);
			        } catch (JsonProcessingException e) {
						Sentry.captureException(e);
			        	throw new GeneralCustomException("ERROR", "Invalid data in identity verification request - " + mapErrorList);
			        }
				   throw new GeneralCustomException("ERROR", "Invalid data in identity verification request - " + json);
			   }
			   
	   } catch(GeneralCustomException e) {
			   Sentry.captureException(e);
		   throw e;
	   } catch(Exception e) {
			   Sentry.captureException(e);
		   log.error("Exception occured while validating the identity verification request");
		   throw e;
	   }
   }
	
	
}
