package com.paywallet.userservice.user.aspect;

import com.paywallet.userservice.user.enums.BorrowerEventEnum;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.enums.ProgressLevel;
import com.paywallet.userservice.user.model.*;
import com.paywallet.userservice.user.model.wrapperAPI.*;
import com.paywallet.userservice.user.util.CommonUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
@Configuration
public class BorrowerSentryAspect {
    @Autowired
    CommonUtil commonUtil;
    @Autowired
    RequestIdUtil requestIdUtil;

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.createCustomer(..)) && args(customer,requestId,request)")
    public void createCustomer(CreateCustomerRequest customer, String requestId, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry createCustomer  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomerByMobile(..)) && args(cellPhone,request)")
    public void getCustomerByMobile(String cellPhone, HttpServletRequest request) {
        try{
            commonUtil.addSentryTransactionIdentifier(cellPhone);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry getCustomerByMobile  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomer(..)) && args(customerId,request)")
    public void getCustomer(String customerId, HttpServletRequest request) {
        try{
            commonUtil.addSentryTransactionIdentifier(customerId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry getCustomer  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getAccountDetails(..)) && args(cellPhone,request)")
    public void getAccountDetails(String cellPhone, HttpServletRequest request) {
        try{
            commonUtil.addSentryTransactionIdentifier(cellPhone);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry getAccountDetails  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.validateAccount(..)) && args(validateAccountRequest,request)")
    public void validateAccount(ValidateAccountRequest validateAccountRequest, HttpServletRequest request) {
        try{
            commonUtil.addSentryTransactionIdentifier(validateAccountRequest.getCellPhone());
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry validateAccount  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomer(..)) && args(updateCustomerRequest,request)")
    public void updateCustomer(UpdateCustomerRequestDTO updateCustomerRequest, HttpServletRequest request) {
        try{
            commonUtil.addSentryTransactionIdentifier(updateCustomerRequest.getCellPhone());
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry updateCustomer  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomerMobileNo(..)) && args(updateCustomerDetailsDTO,requestId,request)")
    public void updateCustomerMobileNo(UpdateCustomerMobileNoDTO updateCustomerDetailsDTO, String requestId, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry updateCustomerMobileNo  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomerEmailId(..)) && args(updateCustomerEmailIdDTO,requestId,request)")
    public void updateCustomerEmailId(UpdateCustomerEmailIdDTO updateCustomerEmailIdDTO, String requestId, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry updateCustomerEmailId  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerCredentialVerificationController.updateCustomerCredentialVerificationStatus(..)) && args(requestId,customerCredentialStatus,request)")
    public void updateCustomerCredentialVerificationStatus(String requestId,UpdateCustomerCredentialStatus customerCredentialStatus, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry updateCustomerCredentialVerificationStatus  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.updateCustomerCredentials(..)) && args(updateCustomerCredentialsModel,requestId,request)")
    public void updateCustomerCredentialVerificationStatus(UpdateCustomerCredentialsModel updateCustomerCredentialsModel,String requestId, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry updateCustomerCredentialVerificationStatus  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.inititateDepositAllocation(..)) && args(depositAllocationRequestWrapperModel,requestId,request)")
    public void updateCustomerCredentialVerificationStatus(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry updateCustomerCredentialVerificationStatus  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateEmploymentVerification(..)) && args(employmentVerificationRequestWrapperModel,requestId,request)")
    public void initiateEmploymentVerification(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry initiateEmploymentVerification  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIncomeVerification(..)) && args(incomeVerificationRequestWrapperModel,requestId,request)")
    public void initiateIncomeVerification(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry initiateIncomeVerification  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIdentityVerification(..)) && args(identityVerificationRequestWrapperModel,requestId,request)")
    public void initiateIdentityVerification(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry initiateIdentityVerification  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.EmploymentVerificationWrapperAPIController.retryEmploymentVerification(..)) && args(requestId,empVerificationRequestDTO,request)")
    public void retryEmploymentVerification(String requestId, WrapperRetryRequest empVerificationRequestDTO, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry retryEmploymentVerification  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IdentityVerificationWrapperAPIController.retryIdentityVerification(..)) && args(requestId,identityVerificationRequestDTO,request)")
    public void retryIdentityVerification(String requestId, WrapperRetryRequest identityVerificationRequestDTO, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry retryIdentityVerification  : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IncomeVerificationWrapperAPIController.retryIncomeVerification(..)) && args(requestId,incomeVerificationRequestDTO,request)")
    public void retryIncomeVerification(String requestId, WrapperRetryRequest incomeVerificationRequestDTO, HttpServletRequest request) {
        try{
            String decodeRequestId = requestIdUtil.getDecodedRequestID(requestId).get();
            commonUtil.addSentryTransactionIdentifier(decodeRequestId);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error creating aspect for sentry retryIncomeVerification  : {}",e);
        }
    }


}
