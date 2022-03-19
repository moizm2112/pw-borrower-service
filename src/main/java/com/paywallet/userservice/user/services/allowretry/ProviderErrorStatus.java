package com.paywallet.userservice.user.services.allowretry;

import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import org.springframework.stereotype.Service;

@Service
public class ProviderErrorStatus implements  AllowRetryService{


    @Override
    public String checkForRetryStatus(RequestIdDetails requestIdDetails) throws RetryException {

        // Need to add the provider error code logic
        return "";
    }
}
