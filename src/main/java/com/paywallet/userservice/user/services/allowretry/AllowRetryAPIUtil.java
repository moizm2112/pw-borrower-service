package com.paywallet.userservice.user.services.allowretry;

import com.paywallet.userservice.user.constant.RetryAPIConstants;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
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
    public void checkForRetryStatus(RequestIdDetails requestIdDetails, FlowTypeEnum flowType) throws RetryException {

        String requestID = requestIdDetails.getRequestId();

        log.info(" checking whether {} retry API is allowed or not :: request ID : {} ", flowType.name(),requestID);
        this.checkRetryAllowedOrNot(requestIdDetails,flowType);

        String sdkLoginStatus = loginSDKStatus.checkForRetryStatus(requestIdDetails);
        log.info(" Login SDK allow retry status : {}  request ID : {} ", sdkLoginStatus, requestID);

        String notificationStatus = notificationErrorStatus.checkForRetryStatus(requestIdDetails);
        log.info(" Notification error retry status : {} request ID : {} ", notificationStatus, requestID);

        String providerStatus = providerErrorStatus.checkForRetryStatus(requestIdDetails);
        log.info(" Provider error allow retry status : {} request ID : {}", providerStatus, requestID);

    }


    private void checkRetryAllowedOrNot(RequestIdDetails requestIdDetails, FlowTypeEnum flowType) throws RetryException {
        if (!(null != requestIdDetails.getFlowType() && requestIdDetails.getFlowType().contains(flowType))) {
            throw new RetryException(getErrorMessage(flowType));
        }
    }


    private String getErrorMessage(FlowTypeEnum flowType) {

        String message = "";
        switch (flowType) {
            case INCOME_VERIFICATION:
                message = RetryAPIConstants.INCOME_RETRY_NOT_ALLOWED;
                break;
            case IDENTITY_VERIFICATION:
                message = RetryAPIConstants.IDENTITY_RETRY_NOT_ALLOWED;
                break;
            case EMPLOYMENT_VERIFICATION:
                message = RetryAPIConstants.EMP_RETRY_NOT_ALLOWED;
                break;
        }
        return message;
    }

}
