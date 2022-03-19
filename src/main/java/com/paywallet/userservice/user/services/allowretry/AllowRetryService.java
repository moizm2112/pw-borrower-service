package com.paywallet.userservice.user.services.allowretry;

import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public interface AllowRetryService {
    String checkForRetryStatus(RequestIdDetails requestIdDetails) throws RetryException;
}
