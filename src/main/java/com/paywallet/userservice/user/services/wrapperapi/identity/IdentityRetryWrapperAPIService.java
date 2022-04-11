package com.paywallet.userservice.user.services.wrapperapi.identity;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.WrapperRetryRequest;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityVerificationResponseDTO;
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
public class IdentityRetryWrapperAPIService {

    @Autowired
    RequestIdUtil requestIdUtil;
    
    @Autowired
    CustomerServiceHelper customerServiceHelper;
    
    @Autowired
    KafkaPublisherUtil kafkaPublisherUtil;

    @Autowired
    CustomerService customerService;

    @Autowired
    AllowRetryAPIUtil allowRetryAPIUtil;
    
    @Autowired
    CustomerWrapperAPIService customerWrapperService;
    
    
    @Autowired
    CustomerFieldValidator customerFieldValidator;
    @Autowired
    RestTemplate restTemplate;

    /**
     * checking for allow retry status, if retry is allowed, then re-initiating identity verification
     **/
    public IdentityResponseInfo retryIdentityVerification(String requestId, WrapperRetryRequest identityVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails);
        // initiate retry code logic -> need to add
        CustomerDetails customer = initiateIdentityVerification(requestIdDetails, requestId, identityVerificationRequestDTO);
        return this.prepareIdentityResponseInfo(customer,identityVerificationRequestDTO);

    }
    
    public CustomerDetails initiateIdentityVerification(RequestIdDetails requestIdDetails, String requestId, WrapperRetryRequest identityVerificationRequestDTO) throws RetryException {
    	log.info(" Inside initiateIdentityVerification, with RequestDetails as ::" , requestIdDetails);
    	CustomerDetails customer = Optional.ofNullable(customerService.getCustomer(requestIdDetails.getUserId()))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Customer not found"));
    	log.info(" Received the CustomerDetails ::" , customer);
    	requestIdDetails = validateInput( customer, requestId,  requestIdDetails, identityVerificationRequestDTO) ;
    	kafkaPublisherUtil.publishLinkServiceInfo(requestIdDetails,customer, FlowTypeEnum.IDENTITY_VERIFICATION);
    	return customer;
    }


    public IdentityResponseInfo prepareIdentityResponseInfo(CustomerDetails customer, WrapperRetryRequest identityVerReqDTO) {
        // Need to change the reading fields from request
        return IdentityResponseInfo.builder()
                .employer(identityVerReqDTO.getEmployerId())
                .emailId(identityVerReqDTO.getEmailId())
                .cellPhone(customer.getPersonalProfile().getCellPhone())
                .build();
    }

    public IdentityVerificationResponseDTO prepareResponseDTO(IdentityResponseInfo identityResponseInfo,String status, String message, String requestURI) {
        return IdentityVerificationResponseDTO.builder()
                .data(identityResponseInfo)
                .message(message)
                .path(requestURI)
                .timeStamp(new Date())
                .status(status)
                .build();
    }
    
    public RequestIdDetails validateInput(CustomerDetails customer,String requestId, RequestIdDetails requestIdDetails,
    		WrapperRetryRequest identityVerificationRequestDTO) throws RetryException {
    	log.info("Inside validateInput");
    	log.info(customer.getPersonalProfile().getCellPhone());
    	log.info(customer.getPersonalProfile().getEmailId());
    	log.info(identityVerificationRequestDTO.getCellPhone());
    	log.info(identityVerificationRequestDTO.getEmailId());

    	customerWrapperService.validateMobileFromRequest(identityVerificationRequestDTO.getCellPhone(),"Identity");
    	//Check if the employer Id in the Request Table has been changed with new employerId in the Retry Request. If yes, call the select employer
    	if(! requestIdDetails.getEmployerPWId().equals(identityVerificationRequestDTO.getEmployerId())) {
    		log.info("Employer Changed. Updating the new employer");
    		customerServiceHelper.getEmployerDetailsBasedOnEmployerId(identityVerificationRequestDTO.getEmployerId(),requestId);
    		requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
    	}


        if((! customer.getPersonalProfile().getCellPhone().contains(identityVerificationRequestDTO.getCellPhone()))&&
                (!customer.getPersonalProfile().getEmailId().equals(identityVerificationRequestDTO.getEmailId()))){
            throw new RetryException("Both Email ID and Mobile No. does not match with the request ID.");
        }

        if(! customer.getPersonalProfile().getCellPhone().contains(identityVerificationRequestDTO.getCellPhone())) {
            throw new RetryException("Mobile No. does not match with the request ID.");
        }

        if(!customer.getPersonalProfile().getEmailId().equals(identityVerificationRequestDTO.getEmailId())) {
            throw new  RetryException("Email Id does not match with the request ID.");
        }

    	return requestIdDetails;
    }
}
