package com.paywallet.userservice.user.services.wrapperapi.identity;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityVerificationResponseDTO;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.CustomerServiceHelper;
import com.paywallet.userservice.user.services.allowretry.AllowRetryAPIUtil;
import com.paywallet.userservice.user.util.KafkaPublisherUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Date;
import java.util.Optional;

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
    public IdentityResponseInfo retryIdentityVerification(String requestId, IdentityVerificationRequestDTO identityVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails);
        // initiate retry code logic -> need to add
        initiateIdentityVerification(requestIdDetails);
        return this.prepareIdentityResponseInfo(identityVerificationRequestDTO);

    }
    
    public void initiateIdentityVerification(RequestIdDetails requestIdDetails) {
    	log.info(" Inside initiateIdentityVerification, with RequestDetails as ::" , requestIdDetails);
    	CustomerDetails customer = Optional.ofNullable(customerService.getCustomer(requestIdDetails.getUserId()))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Customer not found"));
    	log.info(" Received the CustomerDetails ::" , customer);
    	kafkaPublisherUtil.publishLinkServiceInfo(requestIdDetails,customer, FlowTypeEnum.EMPLOYMENT_VERIFICATION);
    }


    public IdentityResponseInfo prepareIdentityResponseInfo(IdentityVerificationRequestDTO identityVerReqDTO) {
        // Need to change the reading fields from request
        return IdentityResponseInfo.builder()
                .employer(identityVerReqDTO.getEmployerId())
                .emailId(identityVerReqDTO.getEmailId())
                .mobileNo(identityVerReqDTO.getMobileNo())
                .build();
    }

    public IdentityVerificationResponseDTO prepareResponseDTO(IdentityResponseInfo identityResponseInfo, String message, String  value, String requestURI) {
        return IdentityVerificationResponseDTO.builder()
                .data(identityResponseInfo)
                .message(message)
                .path(requestURI)
                .timeStamp(new Date())
                .status(value)
                .build();
    }
}
