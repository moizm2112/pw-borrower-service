package com.paywallet.userservice.user.services.wrapperapi.employement;

import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationResponseDTO;
import com.paywallet.userservice.user.services.allowretry.AllowRetryAPIUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Date;

@Service
@Slf4j
public class EmploymentRetryWrapperAPIService {

    @Autowired
    RequestIdUtil requestIdUtil;

    @Autowired
    AllowRetryAPIUtil allowRetryAPIUtil;

    /**
     * checking for allow retry status, if retry is allowed, then re-initiating employment verification
     **/
    public EmploymentResponseInfo retryEmploymentVerification(String requestId, EmploymentVerificationRequestDTO empVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails= requestIdUtil.fetchRequestIdDetails(requestId);
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails);
        // initiate retry code logic -> need to add
        return this.prepareEmploymentResponseInfo(empVerificationRequestDTO);

    }


    public EmploymentResponseInfo prepareEmploymentResponseInfo(EmploymentVerificationRequestDTO empVerificationRequestDTO){
       // Need to change the reading fields from request
        return EmploymentResponseInfo.builder()
                .employer(empVerificationRequestDTO.getEmployerId())
                .emailId(empVerificationRequestDTO.getEmailId())
                .mobileNo(empVerificationRequestDTO.getMobileNo())
                .build();
    }

    public EmploymentVerificationResponseDTO prepareResponseDTO(EmploymentResponseInfo employmentResponseInfo, String code, int value, String requestURI, String message) {
        return EmploymentVerificationResponseDTO.builder()
                .data(employmentResponseInfo)
                .message(message)
                .path(requestURI)
                .timeStamp(new Date())
                .status(code)
                .build();
    }
}
