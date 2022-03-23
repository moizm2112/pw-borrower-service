package com.paywallet.userservice.user.services.wrapperapi.identity;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityVerificationResponseDTO;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.CustomerServiceHelper;
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

    /**
     * checking for allow retry status, if retry is allowed, then re-initiating identity verification
     **/
    public IdentityResponseInfo retryIdentityVerification(String requestId, IdentityVerificationRequestWrapperModel identityVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails);
        // initiate retry code logic -> need to add
        initiateIdentityVerification(requestIdDetails, requestId, identityVerificationRequestDTO);
        return this.prepareIdentityResponseInfo(identityVerificationRequestDTO);

    }
    
    public void initiateIdentityVerification(RequestIdDetails requestIdDetails, String requestId, IdentityVerificationRequestWrapperModel identityVerificationRequestDTO) {
    	log.info(" Inside initiateIdentityVerification, with RequestDetails as ::" , requestIdDetails);
    	CustomerDetails customer = Optional.ofNullable(customerService.getCustomer(requestIdDetails.getUserId()))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Customer not found"));
    	log.info(" Received the CustomerDetails ::" , customer);
    	requestIdDetails = validateInput( customer, requestId,  requestIdDetails, identityVerificationRequestDTO) ;
    	kafkaPublisherUtil.publishLinkServiceInfo(requestIdDetails,customer, FlowTypeEnum.IDENTITY_VERIFICATION);
    }


    public IdentityResponseInfo prepareIdentityResponseInfo(IdentityVerificationRequestWrapperModel identityVerReqDTO) {
        // Need to change the reading fields from request
        return IdentityResponseInfo.builder()
                .employer(identityVerReqDTO.getEmployerId())
                .emailId(identityVerReqDTO.getEmailId())
                .cellPhone(identityVerReqDTO.getCellPhone())
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
    		IdentityVerificationRequestWrapperModel identityVerificationRequestDTO) throws RetryException {
    	log.info("Inside validateInput");
    	log.info(customer.getPersonalProfile().getCellPhone());
    	log.info(customer.getPersonalProfile().getEmailId());
    	log.info(identityVerificationRequestDTO.getCellPhone());
    	log.info(identityVerificationRequestDTO.getEmailId());

    	//Check if the employer Id in the Request Table has been changed with new employerId in the Retry Request. If yes, call the select employer
    	if(! requestIdDetails.getEmployerPWId().equals(identityVerificationRequestDTO.getEmployerId())) {
    		log.info("Employer Changed. Updating the new employer");
    		customerServiceHelper.getEmployerDetailsBasedOnEmployerId(identityVerificationRequestDTO.getEmployerId(),requestId);
    		requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
    	}


        if(! customer.getPersonalProfile().getCellPhone().equals(identityVerificationRequestDTO.getCellPhone())) {
            throw new RetryException("Mobile No. does not match with the request ID.");
        }

        if(!customer.getPersonalProfile().getEmailId().equals(identityVerificationRequestDTO.getEmailId())) {
            throw new  RetryException("Email Id does not match with the request ID.");
        }

    	return requestIdDetails;
    }
}
