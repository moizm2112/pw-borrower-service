package com.paywallet.userservice.user.services.allowretry;

import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AllowRetryAPIUtil {

    @Autowired
    LoginSDKStatus loginSDKStatus;

    @Autowired
    NotificationErrorStatus notificationErrorStatus;

    @Autowired
    ProviderErrorStatus providerErrorStatus;

    /**
     * checking previous request login status, notification status, provider error status
     *
     * @param requestIdDetails
     * @throws RetryException
     */
    public void checkForRetryStatus(RequestIdDetails requestIdDetails) throws RetryException {

        String requestID = requestIdDetails.getRequestId();

        String sdkLoginStatus = loginSDKStatus.checkForRetryStatus(requestIdDetails);
        log.info(" Login SDK allow retry status : {}  request ID : {} ", sdkLoginStatus, requestID);

        String notificationStatus = notificationErrorStatus.checkForRetryStatus(requestIdDetails);
        log.info(" Notification error retry status : {} request ID : {} ", notificationStatus, requestID);

        String providerStatus = providerErrorStatus.checkForRetryStatus(requestIdDetails);
        log.info(" Provider error allow retry status : {} request ID : {}", providerStatus, requestID);

    }

}
