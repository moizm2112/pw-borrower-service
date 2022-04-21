package com.paywallet.userservice.user.controller.wrapperAPI;

import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.WrapperRetryRequest;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationResponseDTO;
import com.paywallet.userservice.user.services.wrapperapi.employement.EmploymentRetryWrapperAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.paywallet.userservice.user.constant.AppConstants.BASE_PATH;
import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;
import static com.paywallet.userservice.user.constant.URIConstants.EMP_VERIFICATION_RETRY;

@RestController
@Slf4j
@RequestMapping(BASE_PATH)
public class EmploymentVerificationWrapperAPIController {

    @Autowired
    EmploymentRetryWrapperAPIService employmentRetryWrapperAPIService;


    /**
     * API for Re-initiate/Retry Employment Verification
     *
     * @param requestId
     * @param empVerificationRequestDTO
     * @param request
     * @return
     * @throws RequestIdNotFoundException
     * @throws RetryException
     */
    @PostMapping(EMP_VERIFICATION_RETRY)
    public EmploymentVerificationResponseDTO retryEmploymentVerification(@RequestHeader(REQUEST_ID) String requestId,
    															@Valid @RequestBody WrapperRetryRequest empVerificationRequestDTO,
                                                                         HttpServletRequest request) throws RequestIdNotFoundException, RetryException {
        log.debug("Employment Verification retry request received : {}  request ID : {} ", empVerificationRequestDTO, requestId);
        
        EmploymentResponseInfo employmentResponseInfo = employmentRetryWrapperAPIService.retryEmploymentVerification(requestId, empVerificationRequestDTO);
        return employmentRetryWrapperAPIService.prepareResponseDTO(employmentResponseInfo, CommonEnum.SUCCESS_STATUS_MSG.name(),
                 request.getRequestURI(), CommonEnum.COMMON_RETRY_SUCCESS_MSG.getMessage());
    }


}
