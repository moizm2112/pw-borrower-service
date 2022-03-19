package com.paywallet.userservice.user.controller.wrapperAPI;

import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.identity.IdentityVerificationResponseDTO;
import com.paywallet.userservice.user.services.wrapperapi.identity.IdentityRetryWrapperAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.paywallet.userservice.user.constant.AppConstants.BASE_PATH;
import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;
import static com.paywallet.userservice.user.constant.URIConstants.IDENTITY_VERIFICATION_RETRY;

@RestController
@Slf4j
@RequestMapping(BASE_PATH)
public class IdentityVerificationWrapperAPIController {

    @Autowired
    IdentityRetryWrapperAPIService identityRetryWrapperAPIService;

    /**
     * API for Re-initiate/Retry Identity Verification
     *
     * @param requestId
     * @param identityVerificationRequestDTO
     * @param request
     * @return
     * @throws RequestIdNotFoundException
     * @throws RetryException
     */
    @PostMapping(IDENTITY_VERIFICATION_RETRY)
    public IdentityVerificationResponseDTO retryIdentityVerification(@RequestHeader(REQUEST_ID) String requestId,
                                                                     @RequestBody IdentityVerificationRequestDTO identityVerificationRequestDTO,
                                                                     HttpServletRequest request) throws RequestIdNotFoundException, RetryException {

        log.debug("Identity Verification retry request received : {}  request ID : {} ", identityVerificationRequestDTO, requestId);
        IdentityResponseInfo identityResponseInfo = identityRetryWrapperAPIService.retryIdentityVerification(requestId, identityVerificationRequestDTO);
        return identityRetryWrapperAPIService.prepareResponseDTO(identityResponseInfo, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
                HttpStatus.OK.value(), request.getRequestURI());

    }

}
