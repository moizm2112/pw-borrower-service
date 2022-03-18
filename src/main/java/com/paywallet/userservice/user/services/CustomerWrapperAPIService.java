package com.paywallet.userservice.user.services;

import static com.paywallet.userservice.user.constant.AppConstants.BANKABA_LENGTH_VALIDATION_MESSAGE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
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
import com.paywallet.userservice.user.entities.PersonalProfile;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.exception.FineractAPIException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.SMSAndEmailNotificationException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.CallbackURL;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.CustomerRequestFields;
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
import com.paywallet.userservice.user.util.KafkaPublisherUtil;

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
    
	public UpdateCustomerCredentialsResponse updateCustomerCredentials(UpdateCustomerCredentialsModel customerCredentialsModel, String requestId) 
			throws CustomerNotFoundException, RequestIdNotFoundException {
		
		UpdateCustomerCredentialsResponse updateCustomerCredentialsResponse =  new UpdateCustomerCredentialsResponse();
		UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponse =  null;
		try {
			
			if(StringUtils.isNotBlank(customerCredentialsModel.getNewEmailId())) {
				UpdateCustomerEmailIdDTO updateCustomerEmailIdDTO = setDTOForEmailUpdate(customerCredentialsModel);
				updateCustomerDetailsResponse = customerService.updateCustomerEmailId(updateCustomerEmailIdDTO, requestId);
				setUpdateCustomerCredentialsEmailResponse(updateCustomerDetailsResponse, updateCustomerCredentialsResponse);
			}
			else {
				updateCustomerCredentialsResponse.setEmailId(StringUtils.EMPTY);
				updateCustomerCredentialsResponse.setEmailIdVerified(StringUtils.EMPTY);
			}
			
			if(StringUtils.isNotBlank(customerCredentialsModel.getNewMobileNo())) {
				UpdateCustomerMobileNoDTO updateCustomerMobileNoDTO = setDTOForMobileNoUpdate(customerCredentialsModel);
				updateCustomerDetailsResponse = customerService.updateCustomerMobileNo(updateCustomerMobileNoDTO, requestId);
				setUpdateCustomerCredentialsMobileResponse(updateCustomerDetailsResponse, updateCustomerCredentialsResponse);
			}
			else {
				updateCustomerCredentialsResponse.setMobileNo(updateCustomerDetailsResponse.getMobileNo());
				updateCustomerCredentialsResponse.setMobileNoVerified(StringUtils.EMPTY);
			}
			updateCustomerCredentialsResponse.setRequestId(requestId);
		}
		catch(CustomerNotFoundException | RequestIdNotFoundException | FineractAPIException | GeneralCustomException e) {
			log.error("Exception occured while updating customer credentials " + e.getMessage());
			throw e;
		}
		catch(Exception e) {
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
		updateCustomerCredentialsResponse.setMobileNo(updateCustomerDetailsResponse.getMobileNo());
		
		/* NEED TO UPDATE IT WITH ACTUAL VALUE AFTER UNDERSTANDING THE REQUIREMENTS */
		updateCustomerCredentialsResponse.setMobileNoVerified(StringUtils.EMPTY);
	}
	
	public UpdateCustomerEmailIdDTO setDTOForEmailUpdate(UpdateCustomerCredentialsModel customerCredentialsModel) {
		
		UpdateCustomerEmailIdDTO updateCustomerEmailId = new UpdateCustomerEmailIdDTO();
		
		updateCustomerEmailId.setMobileNo(customerCredentialsModel.getMobileNo());
		updateCustomerEmailId.setEmailId(customerCredentialsModel.getEmailId());
		updateCustomerEmailId.setNewEmailId(customerCredentialsModel.getNewEmailId());
		
		return updateCustomerEmailId;
	}
	
	public UpdateCustomerMobileNoDTO setDTOForMobileNoUpdate(UpdateCustomerCredentialsModel customerCredentialsModel) {
		
		UpdateCustomerMobileNoDTO updateCustomerMobileNoDTO = new UpdateCustomerMobileNoDTO();
		
		updateCustomerMobileNoDTO.setMobileNo(customerCredentialsModel.getMobileNo());
		updateCustomerMobileNoDTO.setNewMobileNo(customerCredentialsModel.getNewMobileNo());
		
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
			customer.setFirstName(depositAllocationRequestWrapperModel.getFirstName());
			customer.setLastName(depositAllocationRequestWrapperModel.getLastName());
			customer.setMobileNo(depositAllocationRequestWrapperModel.getMobileNo());
			customer.setEmailId(depositAllocationRequestWrapperModel.getEmailId());
			customer.setFirstDateOfPayment(depositAllocationRequestWrapperModel.getFirstDateOfPayment());
			customer.setRepaymentFrequency(depositAllocationRequestWrapperModel.getRepaymentFrequency());
			customer.setTotalNoOfRepayment(depositAllocationRequestWrapperModel.getTotalNoOfRepayment());
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
			customer.setMobileNo(employmentVerificationRequestWrapperModel.getMobileNo());
			customer.setEmailId(employmentVerificationRequestWrapperModel.getEmailId());
			if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getEmploymentCallbackUrl())) {
				CallbackURL callbackURL = new CallbackURL();
				List<String> employmentCallbackUrls =  new ArrayList<String>();
				employmentCallbackUrls.add(employmentVerificationRequestWrapperModel.getEmploymentCallbackUrl());
				callbackURL.setEmploymentCallbackUrls(employmentCallbackUrls);
				customer.setCallbackURLs(callbackURL);
			}
			customer.setFirstDateOfPayment(StringUtils.EMPTY);
			customer.setRepaymentFrequency(StringUtils.EMPTY);
			customer.setTotalNoOfRepayment(0);
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
			customer.setMobileNo(identityVerificationRequestWrapperModel.getMobileNo());
			customer.setEmailId(identityVerificationRequestWrapperModel.getEmailId());
			if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getIdentityCallbackUrl())) {
				CallbackURL callbackURL = new CallbackURL();
				List<String> identityCallbackUrls =  new ArrayList<String>();
				identityCallbackUrls.add(identityVerificationRequestWrapperModel.getIdentityCallbackUrl());
				callbackURL.setIdentityCallbackUrls(identityCallbackUrls);
				customer.setCallbackURLs(callbackURL);
			}
			customer.setFirstDateOfPayment(StringUtils.EMPTY);
			customer.setRepaymentFrequency(StringUtils.EMPTY);
			customer.setTotalNoOfRepayment(0);
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
			customer.setMobileNo(incomeVerificationRequestWrapperModel.getMobileNo());
			customer.setEmailId(incomeVerificationRequestWrapperModel.getEmailId());
			if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getIncomeCallbackUrl())) {
				CallbackURL callbackURL = new CallbackURL();
				List<String> incomeCallbackUrls =  new ArrayList<String>();
				incomeCallbackUrls.add(incomeVerificationRequestWrapperModel.getIncomeCallbackUrl());
				callbackURL.setIncomeCallbackUrls(incomeCallbackUrls);
				customer.setCallbackURLs(callbackURL);
			}
			customer.setFirstDateOfPayment(StringUtils.EMPTY);
			customer.setRepaymentFrequency(StringUtils.EMPTY);
			customer.setTotalNoOfRepayment(0);
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
		DepositAllocationResponseWrapperModel depositAllocationResponse = setDepositAllocationResponse(customerDetails);
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
	
	public DepositAllocationResponseWrapperModel setDepositAllocationResponse(CustomerDetails customerDetails) {
		DepositAllocationResponseWrapperModel depositAllocationResponseModel = new DepositAllocationResponseWrapperModel();
		depositAllocationResponseModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		depositAllocationResponseModel.setMobileNo(customerDetails.getPersonalProfile().getMobileNo());
		depositAllocationResponseModel.setVirtualAccountNumber(customerDetails.getVirtualAccount());
		depositAllocationResponseModel.setVirtualAccountABANumber(customerDetails.getAccountABANumber());
		depositAllocationResponseModel.setVirtualAccountId(customerDetails.getVirtualAccountId());
		depositAllocationResponseModel.setTotalNoOfRepayment(customerDetails.getTotalNoOfRepayment());
		depositAllocationResponseModel.setInstallmentAmount(customerDetails.getInstallmentAmount());
		return depositAllocationResponseModel;
	}
	
	public EmploymentVerificationResponseWrapperModel setEmploymentVerificationResponse(CustomerDetails customerDetails, EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel) {
		EmploymentVerificationResponseWrapperModel employmentVerificationResponseModel = new EmploymentVerificationResponseWrapperModel();
		employmentVerificationResponseModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		employmentVerificationResponseModel.setMobileNo(customerDetails.getPersonalProfile().getMobileNo());
		employmentVerificationResponseModel.setLenderName(customerDetails.getLender());
		employmentVerificationResponseModel.setEmployer(customerDetails.getEmployer());
		employmentVerificationResponseModel.setEmploymentCallbackUrl(employmentVerificationRequestWrapperModel.getEmploymentCallbackUrl());
		employmentVerificationResponseModel.setFirstName(customerDetails.getPersonalProfile().getFirstName());
		employmentVerificationResponseModel.setLastName(customerDetails.getPersonalProfile().getLastName());
		return employmentVerificationResponseModel;
	}
	
	public IncomeVerificationResponseWrapperModel setIncomeVerificationResponse(CustomerDetails customerDetails, IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel) {
		IncomeVerificationResponseWrapperModel incomeVerificationResponseModel = new IncomeVerificationResponseWrapperModel();
		incomeVerificationResponseModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		incomeVerificationResponseModel.setMobileNo(customerDetails.getPersonalProfile().getMobileNo());
		incomeVerificationResponseModel.setNumberOfMonthsRequested(incomeVerificationRequestWrapperModel.getNumberOfMonthsRequested());
		incomeVerificationResponseModel.setEmployer(customerDetails.getEmployer());
		incomeVerificationResponseModel.setIncomeCallbackUrl(incomeVerificationRequestWrapperModel.getIncomeCallbackUrl());
		incomeVerificationResponseModel.setFirstName(customerDetails.getPersonalProfile().getFirstName());
		incomeVerificationResponseModel.setLastName(customerDetails.getPersonalProfile().getLastName());
		return incomeVerificationResponseModel;
	}
	
	public IdentityVerificationResponseWrapperModel setIdentityVerificationResponse(CustomerDetails customerDetails, IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel) {
		IdentityVerificationResponseWrapperModel identityVerificationResponseWrapperModel = new IdentityVerificationResponseWrapperModel();
		identityVerificationResponseWrapperModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		identityVerificationResponseWrapperModel.setMobileNo(customerDetails.getPersonalProfile().getMobileNo());
		identityVerificationResponseWrapperModel.setLast4TIN(customerDetails.getPersonalProfile().getLast4TIN());
		identityVerificationResponseWrapperModel.setEmployer(customerDetails.getEmployer());
		identityVerificationResponseWrapperModel.setIdentityCallbackUrl(identityVerificationRequestWrapperModel.getIdentityCallbackUrl());
		identityVerificationResponseWrapperModel.setFirstName(customerDetails.getPersonalProfile().getFirstName());
		identityVerificationResponseWrapperModel.setLastName(customerDetails.getPersonalProfile().getLastName());
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
		   if(StringUtils.isNotBlank(allocationRequest.getMobileNo()) || StringUtils.isBlank(allocationRequest.getMobileNo())) {
			   List<String> errorList = customerFieldValidator.validateMobileNo(allocationRequest.getMobileNo());
			   if(errorList.size() > 0)
				   mapErrorList.put("Mobile Number", errorList);
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
			   List<String> errorList = customerFieldValidator.validateEmailId(allocationRequest.getEmailId(), customerRepository, allocationRequest.getMobileNo());
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
		   if(allocationRequest.getTotalNoOfRepayment() == null || allocationRequest.getTotalNoOfRepayment() != null){
			   List<String> errorList = customerFieldValidator.validateTotalNoOfRepayment(allocationRequest.getTotalNoOfRepayment());
			   if(errorList.size() > 0)
				   mapErrorList.put("Total Number Of Repayment", errorList);
		   }else {
			   if("YES".equalsIgnoreCase(lenderConfigInfo.getInvokeAndPublishDepositAllocation().name())) {
				   List<String> errorList = new ArrayList<String>();
				   if (allocationRequest.getTotalNoOfRepayment() == null || allocationRequest.getTotalNoOfRepayment() <= 0) {
					   errorList.add(AppConstants.TOTALNOOFREPAYMENT_MANDATORY_MESSAGE);
					   mapErrorList.put("Total Number Of Repayment", errorList);
				   }
			   }
			   else if(allocationRequest.getTotalNoOfRepayment() != null || allocationRequest.getTotalNoOfRepayment() >= 0) {
				   List<String> errorList = customerFieldValidator.validateTotalNoOfRepayment(allocationRequest.getTotalNoOfRepayment());
				   if(errorList.size() > 0)
					   mapErrorList.put("Total Number Of Repayment", errorList);
			   }
		   }
		   if(allocationRequest.getInstallmentAmount() == null || allocationRequest.getInstallmentAmount() != null) {
			   List<String> errorList = customerFieldValidator.validateInstallmentAmount(allocationRequest.getInstallmentAmount());
			   if(errorList.size() > 0)
				   mapErrorList.put("Installment Amount", errorList);
		   }else {
			   if("YES".equalsIgnoreCase(lenderConfigInfo.getInvokeAndPublishDepositAllocation().name())) {
				   List<String> errorList = new ArrayList<String>();
				   if (allocationRequest.getInstallmentAmount() == null || allocationRequest.getInstallmentAmount() <= 0) {
					   errorList.add(AppConstants.INSTALLMENTAMOUNT_MANDATORY_MESSAGE);
					   mapErrorList.put("Installment amount", errorList);
				   }
			   }
			   else if(allocationRequest.getInstallmentAmount() != null || allocationRequest.getInstallmentAmount() >= 0) {
				   List<String> errorList = customerFieldValidator.validateInstallmentAmount(allocationRequest.getInstallmentAmount());
				   if(errorList.size() > 0)
					   mapErrorList.put("Installment Amount", errorList);
			   }
		   }
		   
		   if(mapErrorList.size() > 0) {
			   ObjectMapper objectMapper = new ObjectMapper();
			   String json = "";
		        try {
		            json = objectMapper.writeValueAsString(mapErrorList);
		            log.error("Invalid data in customer request - " + json);
		        } catch (JsonProcessingException e) {
		        	throw new GeneralCustomException("ERROR", "Invalid data in customer request - " + mapErrorList);
		        }
			   throw new GeneralCustomException("ERROR", "Invalid data in customer request - " + json);
		   }
	   } catch(GeneralCustomException e) {
		   throw e;
	   } catch(Exception e) {
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
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getMobileNo()) || StringUtils.isBlank(employmentVerificationRequestWrapperModel.getMobileNo())) {
			   List<String> errorList = customerFieldValidator.validateMobileNo(employmentVerificationRequestWrapperModel.getMobileNo());
			   if(errorList.size() > 0)
				   mapErrorList.put("Mobile Number", errorList);
		   }
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getEmployerId()) || StringUtils.isBlank(employmentVerificationRequestWrapperModel.getEmployerId())) {
			   List<String> errorList = customerFieldValidator.validateEmployerId(employmentVerificationRequestWrapperModel.getEmployerId(), employerPWId);
			   if(errorList.size() > 0)
				   mapErrorList.put("EmployerId", errorList);
		   }
		   if(StringUtils.isNotBlank(employmentVerificationRequestWrapperModel.getEmailId()) || StringUtils.isBlank(employmentVerificationRequestWrapperModel.getEmailId())) {
			   List<String> errorList = customerFieldValidator.validateEmailId(employmentVerificationRequestWrapperModel.getEmailId(), customerRepository, employmentVerificationRequestWrapperModel.getMobileNo());
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
		        	throw new GeneralCustomException("ERROR", "Invalid data in employment verification request - " + mapErrorList);
		        }
			   throw new GeneralCustomException("ERROR", "Invalid data in employment verification request - " + json);
		   }
	   } catch(GeneralCustomException e) {
		   throw e;
	   } catch(Exception e) {
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
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getMobileNo()) || StringUtils.isBlank(incomeVerificationRequestWrapperModel.getMobileNo())) {
				   List<String> errorList = customerFieldValidator.validateMobileNo(incomeVerificationRequestWrapperModel.getMobileNo());
				   if(errorList.size() > 0)
					   mapErrorList.put("Mobile Number", errorList);
			   }
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getEmployerId()) || StringUtils.isBlank(incomeVerificationRequestWrapperModel.getEmployerId())) {
				   List<String> errorList = customerFieldValidator.validateEmployerId(incomeVerificationRequestWrapperModel.getEmployerId(), employerPWId);
				   if(errorList.size() > 0)
					   mapErrorList.put("EmployerId", errorList);
			   }
			   if(StringUtils.isNotBlank(incomeVerificationRequestWrapperModel.getEmailId()) || StringUtils.isBlank(incomeVerificationRequestWrapperModel.getEmailId())) {
				   List<String> errorList = customerFieldValidator.validateEmailId(incomeVerificationRequestWrapperModel.getEmailId(), customerRepository, incomeVerificationRequestWrapperModel.getMobileNo());
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
			        	throw new GeneralCustomException("ERROR", "Invalid data in income verification request - " + mapErrorList);
			        }
				   throw new GeneralCustomException("ERROR", "Invalid data in income verification request - " + json);
			   }
		   } catch(GeneralCustomException e) {
			   throw e;
		   } catch(Exception e) {
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
			   
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getFirstName())) 
			   {
				   List<String> errorList = customerFieldValidator.validateFirstName(identityVerificationRequestWrapperModel.getFirstName());
				   if(errorList.size() > 0)
					   mapErrorList.put("First Name", errorList);
			   } 
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getLastName())) {
				   List<String> errorList = customerFieldValidator.validateLastName(identityVerificationRequestWrapperModel.getLastName());
				   if(errorList.size() > 0)
					   mapErrorList.put("Last Name", errorList);
			   }
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getMobileNo())) {
				   List<String> errorList = customerFieldValidator.validateMobileNo(identityVerificationRequestWrapperModel.getMobileNo());
				   if(errorList.size() > 0)
					   mapErrorList.put("Mobile Number", errorList);
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
			   if(StringUtils.isNotBlank(identityVerificationRequestWrapperModel.getEmailId())) {
				   List<String> errorList = customerFieldValidator.validateEmailId(identityVerificationRequestWrapperModel.getEmailId(), customerRepository, identityVerificationRequestWrapperModel.getMobileNo());
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
			        	throw new GeneralCustomException("ERROR", "Invalid data in identity verification request - " + mapErrorList);
			        }
				   throw new GeneralCustomException("ERROR", "Invalid data in identity verification request - " + json);
			   }
			   
	   } catch(GeneralCustomException e) {
		   throw e;
	   } catch(Exception e) {
		   log.error("Exception occured while validating the identity verification request");
		   throw e;
	   }
   }
	
	
}
