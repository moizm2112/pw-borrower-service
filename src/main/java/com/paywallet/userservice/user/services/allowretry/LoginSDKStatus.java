package com.paywallet.userservice.user.services.allowretry;

import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.paywallet.userservice.user.constant.RetryAPIConstants.*;
import static com.paywallet.userservice.user.util.NullableWrapperUtil.resolve;

@Service
public class LoginSDKStatus implements AllowRetryService {

    /**
     * checking for login status, if login SDK status is not completed allowing retry, else throwing an error
     *
     * @param requestIdDetails
     * @return
     * @throws RetryException
     */
    @Override
    public String checkForRetryStatus(RequestIdDetails requestIdDetails) throws RetryException {

        StringBuffer message = new StringBuffer();
        Optional<String> loginSdkStatus = resolve(() -> requestIdDetails.getLoginSdkStatus());

        if (loginSdkStatus.isPresent() && loginSdkStatus.get().equalsIgnoreCase(COMPLETED)) {
            throw new RetryException(message.append(LOGIN_COMPLETED_MESSAGE).toString());
        }
        return message.append(LOGIN_NOT_COMPLETED_MESSAGE).append(REQUEST_ID).append(requestIdDetails.getRequestId()).toString();

    }
}
