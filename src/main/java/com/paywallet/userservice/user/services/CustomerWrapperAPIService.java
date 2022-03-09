package com.paywallet.userservice.user.services;

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
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.exception.FineractAPIException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.SMSAndEmailNotificationException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
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
			
			if(StringUtils.isNotBlank(customerCredentialsModel.getNewMobileNo())) {
				UpdateCustomerMobileNoDTO updateCustomerMobileNoDTO = setDTOForMobileNoUpdate(customerCredentialsModel);
				updateCustomerDetailsResponse = customerService.updateCustomerMobileNo(updateCustomerMobileNoDTO, requestId);
				setUpdateCustomerCredentialsMobileResponse(updateCustomerDetailsResponse, updateCustomerCredentialsResponse);
			}
			
			updateCustomerDetailsResponse = Optional.ofNullable(updateCustomerDetailsResponse).orElseThrow(() -> new GeneralCustomException("ERROR", "Exception occured while updating the customer credentials"));
			updateCustomerDetailsResponse.setRequestId(requestId);
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
	
	public DepositAllocationResponseWrapperModel initiateDepositAllocation(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel, String requestId)
			throws CreateCustomerException, GeneralCustomException, ServiceNotAvailableException, RequestIdNotFoundException, SMSAndEmailNotificationException {
		
		CreateCustomerRequest customer = new CreateCustomerRequest();
		setCustomerRequest(depositAllocationRequestWrapperModel, customer);
		
		CustomerDetails customerDetails = customerService.createCustomer(customer, requestId, depositAllocationRequestWrapperModel, true);
		DepositAllocationResponseWrapperModel depositAllocationResponse = setDepositAllocationResponse(customerDetails);
		return depositAllocationResponse;
	}
	
	public DepositAllocationResponseWrapperModel setDepositAllocationResponse(CustomerDetails customerDetails) {
		DepositAllocationResponseWrapperModel depositAllocationResponseModel = new DepositAllocationResponseWrapperModel();
		depositAllocationResponseModel.setEmailId(customerDetails.getPersonalProfile().getEmailId());
		depositAllocationResponseModel.setMobileNo(customerDetails.getPersonalProfile().getMobileNo());
		depositAllocationResponseModel.setExternalVirtualAccount(customerDetails.getVirtualAccount());
		depositAllocationResponseModel.setExternalVirtualAccountABANumber(customerDetails.getVirtualAccountId());
		depositAllocationResponseModel.setTotalNoOfRepayment(customerDetails.getTotalNoOfRepayment());
		depositAllocationResponseModel.setInstallmentAmount(customerDetails.getInstallmentAmount());
		return depositAllocationResponseModel;
	}
	
	
	public void validateDepositAllocationRequest(DepositAllocationRequestWrapperModel allocationRequest, String requestId, String lender, LenderConfigInfo lenderConfigInfo){
		   Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
		   try {
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
				   List<String> errorList = customerFieldValidator.validateEmployerId(allocationRequest.getEmployerId());
				   if(errorList.size() > 0)
					   mapErrorList.put("EmployerId", errorList);
			   }
			   if(StringUtils.isNotBlank(allocationRequest.getAchPullRequest())) {
				   List<String> errorList = customerFieldValidator.validateACHPullRequest(allocationRequest.getAchPullRequest());
				   if(errorList.size() > 0)
					   mapErrorList.put("Address Line1", errorList);
			   }
			   if(StringUtils.isNotBlank(allocationRequest.getAccountVerificationOverride())) {
				   List<String> errorList = customerFieldValidator.validateAccountValidationOverride(allocationRequest.getAccountVerificationOverride());
				   if(errorList.size() > 0)
					   mapErrorList.put("Address Line2", errorList);
			   }
			   if(StringUtils.isNotBlank(allocationRequest.getExternalVirtualAccountABANumber())) {
				   List<String> errorList = customerFieldValidator.validateExternalVirtualAccountABANumber(allocationRequest.getExternalVirtualAccountABANumber());
				   if(errorList.size() > 0)
					   mapErrorList.put("City", errorList);
			   }
			   if(StringUtils.isNotBlank(allocationRequest.getEmailId()) || StringUtils.isBlank(allocationRequest.getEmailId())) {
				   List<String> errorList = customerFieldValidator.validateEmailId(allocationRequest.getEmailId(), customerRepository, allocationRequest.getMobileNo());
				   if(errorList.size() > 0)
					   mapErrorList.put("EmailId", errorList);
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
	
	
}
