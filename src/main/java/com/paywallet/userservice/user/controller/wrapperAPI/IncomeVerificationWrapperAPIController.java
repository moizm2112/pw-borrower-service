package com.paywallet.userservice.user.controller.wrapperAPI;

import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeVerificationResponseDTO;
import com.paywallet.userservice.user.services.wrapperapi.income.IncomeRetryWrapperAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;

import static com.paywallet.userservice.user.constant.AppConstants.BASE_PATH;
import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;
import static com.paywallet.userservice.user.constant.URIConstants.INCOME_VERIFICATION_RETRY;

@RestController
@Slf4j
@RequestMapping(BASE_PATH)
public class IncomeVerificationWrapperAPIController {

    @Autowired
    IncomeRetryWrapperAPIService incomeRetryWrapperAPIService;

    /**
     * API for Re-initiate/Retry Income Verification
     *
     * @param requestId
     * @param incomeVerificationRequestDTO
     * @param request
     * @return
     * @throws RequestIdNotFoundException
     * @throws RetryException
     */
    @PostMapping(INCOME_VERIFICATION_RETRY)
    public IncomeVerificationResponseDTO retryIncomeVerification(@RequestHeader(REQUEST_ID) String requestId,
                                                                 @RequestBody IncomeVerificationRequestDTO incomeVerificationRequestDTO,
                                                                 HttpServletRequest request) throws RequestIdNotFoundException {

        log.debug("Income Verification retry request received : {}  request ID : {} ", incomeVerificationRequestDTO, requestId);
        IncomeResponseInfo incomeResponseInfo;
		try {
			incomeResponseInfo = incomeRetryWrapperAPIService.retryIncomeVerification(requestId, incomeVerificationRequestDTO);
		} catch (ResourceAccessException | RequestIdNotFoundException | GeneralCustomException | RetryException e) {
			return incomeRetryWrapperAPIService.prepareResponseDTO(null, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
	                request.getRequestURI(), CommonEnum.COMMON_RETRY_FALED_MSG.getMessage());
		}
        return incomeRetryWrapperAPIService.prepareResponseDTO(incomeResponseInfo, CommonEnum.FAILED_STATUS_MSG.getMessage(),
                request.getRequestURI(), CommonEnum.COMMON_RETRY_SUCCESS_MSG.getMessage());

    }
}
