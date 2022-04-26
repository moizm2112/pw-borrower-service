package com.paywallet.userservice.user.services.wrapperapi.employement;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.LenderConfigInfo;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.WrapperRetryRequest;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationResponseDTO;
import com.paywallet.userservice.user.services.CustomerFieldValidator;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.CustomerServiceHelper;
import com.paywallet.userservice.user.services.CustomerWrapperAPIService;
import com.paywallet.userservice.user.services.allowretry.AllowRetryAPIUtil;
import com.paywallet.userservice.user.util.KafkaPublisherUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RefreshScope
public class EmploymentRetryWrapperAPIService {

    @Autowired
    RequestIdUtil requestIdUtil;

    @Autowired
    AllowRetryAPIUtil allowRetryAPIUtil;
    
    @Autowired
    CustomerService customerService;
    
    @Autowired
    CustomerWrapperAPIService customerWrapperService;
    
    @Autowired
    CustomerServiceHelper customerServiceHelper;
    
    @Autowired
    KafkaPublisherUtil kafkaPublisherUtil;
    
    @Value("${identifyProviderService.eureka.uri}")
   	private String identifyProviderServiceUri;
    
    @Autowired
    CustomerFieldValidator customerFieldValidator;
    @Autowired
    RestTemplate restTemplate;
       

    /**
     * checking for allow retry status, if retry is allowed, then re-initiating employment verification
     **/
    public EmploymentResponseInfo retryEmploymentVerification(String requestId, WrapperRetryRequest empVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails= requestIdUtil.fetchRequestIdDetails(requestId);
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails,FlowTypeEnum.EMPLOYMENT_VERIFICATION);
        CustomerDetails customer = initiateEmploymentVerification(requestIdDetails, requestId , empVerificationRequestDTO);
        return this.prepareEmploymentResponseInfo(customer, empVerificationRequestDTO);

    }

    public CustomerDetails initiateEmploymentVerification(RequestIdDetails requestIdDetails, String requestId, WrapperRetryRequest empVerificationRequestDTO) throws RetryException {
    	log.info(" Inside initiateEmploymentVerification, with RequestDetails as ::" , requestIdDetails);
    	CustomerDetails customer = Optional.ofNullable(customerService.getCustomer(requestIdDetails.getUserId()))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Customer not found"));
    	log.info(" Received the CustomerDetails ::" , customer);
    	requestIdDetails = validateInput( customer, requestId,  requestIdDetails, empVerificationRequestDTO) ;
    	kafkaPublisherUtil.publishLinkServiceInfo(requestIdDetails,customer, FlowTypeEnum.EMPLOYMENT_VERIFICATION);
    	return customer;
    }

    public EmploymentResponseInfo prepareEmploymentResponseInfo(CustomerDetails customer, WrapperRetryRequest empVerificationRequestDTO){
       // Need to change the reading fields from request
        return EmploymentResponseInfo.builder()
                .employer(empVerificationRequestDTO.getEmployerId())
                .emailId(empVerificationRequestDTO.getEmailId())
                .cellPhone(customer.getPersonalProfile().getCellPhone())
                .build();
    }

    public EmploymentVerificationResponseDTO prepareResponseDTO(EmploymentResponseInfo employmentResponseInfo, String status,String requestURI, String message) {
        return EmploymentVerificationResponseDTO.builder()
                .data(employmentResponseInfo)
                .message(message)
                .path(requestURI)
                .timeStamp(new Date())
                .status(status)
                .build();
    }
    
    public RequestIdDetails validateInput(CustomerDetails customer,String requestId, RequestIdDetails requestIdDetails,
    		WrapperRetryRequest empVerificationRequestDTO) throws RetryException {
    	log.info("Inside validateInput");
    	log.info(customer.getPersonalProfile().getCellPhone());
    	log.info(customer.getPersonalProfile().getEmailId());
    	log.info(empVerificationRequestDTO.getCellPhone());
    	log.info(empVerificationRequestDTO.getEmailId());
    	customerWrapperService.validateMobileFromRequest(empVerificationRequestDTO.getCellPhone(),"Employment");
    	//Check if the employer Id in the Request Table has been changed with new employerId in the Retry Request. If yes, call the select employer
    	if(! requestIdDetails.getEmployerPWId().equals(empVerificationRequestDTO.getEmployerId())) {
    		log.info("Employer Changed. Updating the new employer");
    		customerServiceHelper.getEmployerDetailsBasedOnEmployerId(empVerificationRequestDTO.getEmployerId(),requestId);
    		requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
    	}

        if((! customer.getPersonalProfile().getCellPhone().contains(empVerificationRequestDTO.getCellPhone()))&&
                (!customer.getPersonalProfile().getEmailId().equals(empVerificationRequestDTO.getEmailId()))){
            throw new RetryException("Email Id and/or Cellphone number does not match with the Request Id");
        }

        if(! customer.getPersonalProfile().getCellPhone().contains(empVerificationRequestDTO.getCellPhone())) {
            throw new RetryException("Cellphone number does not match with the Request Id.");
        }

        if(!customer.getPersonalProfile().getEmailId().equals(empVerificationRequestDTO.getEmailId())) {
            throw new RetryException("Email Id does not match with the Request Id.");
        }

    	return requestIdDetails;
    	
    	
    }
}
