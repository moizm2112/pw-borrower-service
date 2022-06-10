package com.paywallet.userservice.user.aspect;

import com.paywallet.userservice.user.enums.BorrowerEventEnum;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.enums.ProgressLevel;
import com.paywallet.userservice.user.event.BorrowerEvent;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.UpdateCustomerCredentialsModel;
import com.paywallet.userservice.user.model.wrapperAPI.*;
import com.paywallet.userservice.user.util.CommonUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
@Configuration
public class BorrowerAspect {

    @Autowired
    BorrowerEvent borrowerEvent;

    @Autowired
    RequestIdUtil requestIdUtil;

    @Autowired
    CommonUtil commonUtil;


    private static final String SERVICE_NAME = "Borrower";

    @Before("execution(* com.paywallet.userservice.user.services.CustomerService.createCustomer(..)) && args(customer,requestId,obj,flowType)")
    public <T> void customerCreateInProgress(CreateCustomerRequest customer, String requestId, T obj, FlowTypeEnum flowType) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_INPROG.getMessage();
            String message = "Customer create INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing customerCreateInProgress events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.CustomerService.createCustomer(..)) && args(customer,requestId,obj,flowType)")
    public <T> void customerCreateFailed(CreateCustomerRequest customer, String requestId, T obj, FlowTypeEnum flowType) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_FAIL.getMessage();
            String message = "Customer create FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing customerCreateFailed events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.CustomerService.createCustomer(..)) && args(customer,requestId,obj,flowType)")
    public <T> void customerCreateSuccess(CreateCustomerRequest customer, String requestId, T obj, FlowTypeEnum flowType) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_SUCC.getMessage();
            String message = "Customer create SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing customerCreateSuccess events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.fetchrequestIdDetails(..)) && args(requestId,identifyProviderServiceUri,restTemplate)")
    public void requestIdCreateInProgress(String requestId, String identifyProviderServiceUri, RestTemplate restTemplate) {
        try{
            String code = BorrowerEventEnum.UMS_GET_REQ_DETAIL_INPROG.getMessage();
            String message = "Request id details create INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing requestIdCreateInProgress events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.fetchrequestIdDetails(..)) && args(requestId,identifyProviderServiceUri,restTemplate)")
    public void requestIdCreateFailed(String requestId, String identifyProviderServiceUri, RestTemplate restTemplate) {
        try{
            String code = BorrowerEventEnum.UMS_GET_REQ_DETAIL_FAIL.getMessage();
            String message = "Request id details create FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing requestIdCreateFailed events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.fetchrequestIdDetails(..)) && args(requestId,identifyProviderServiceUri,restTemplate)")
    public void requestIdCreateSuccess(String requestId, String identifyProviderServiceUri, RestTemplate restTemplate) {
        try{
            String code = BorrowerEventEnum.UMS_GET_REQ_DETAIL_SUCC.getMessage();
            String message = "Request id details create SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing requestIdCreateSuccess events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.updateRequestIdDetails(..)) && args(requestId,customerId,virtualAccountNumber,identifyProviderServiceUri,restTemplate,customerRequest)")
    public void requestIdUpdateInProgress(String requestId, String customerId, String virtualAccountNumber,
                                          String identifyProviderServiceUri, RestTemplate restTemplate, CreateCustomerRequest customerRequest) {
        try{
            String code = BorrowerEventEnum.UMS_UPD_REQ_DETAIL_INPROG.getMessage();
            String message = "Request id details update INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing requestIdUpdateInProgress events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.updateRequestIdDetails(..)) && args(requestId,customerId,virtualAccountNumber,identifyProviderServiceUri,restTemplate,customerRequest)")
    public void requestIdUpdateFailed(String requestId, String customerId, String virtualAccountNumber,
                                      String identifyProviderServiceUri, RestTemplate restTemplate, CreateCustomerRequest customerRequest) {
        try{
            String code = BorrowerEventEnum.UMS_UPD_REQ_DETAIL_FAIL.getMessage();
            String message = "Request id details update FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing requestIdUpdateFailed events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.updateRequestIdDetails(..)) && args(requestId,customerId,virtualAccountNumber,identifyProviderServiceUri,restTemplate,customerRequest)")
    public void requestIdUpdateSuccess(String requestId, String customerId, String virtualAccountNumber,
                                       String identifyProviderServiceUri, RestTemplate restTemplate, CreateCustomerRequest customerRequest) {
        try{
            String code = BorrowerEventEnum.UMS_UPD_REQ_DETAIL_SUCC.getMessage();
            String message = "Request id update details SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing requestIdUpdateSuccess events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.createFineractVirtualAccount(..)) && args(requestId,customer)")
    public void createFineractAccountInProgress(String requestId, CreateCustomerRequest customer) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_FINERACT_VIRTUAL_ACCT_INPROG.getMessage();
            String message = "Create fineract account INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing createFineractAccountInProgress events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.createFineractVirtualAccount(..)) && args(requestId,customer)")
    public void createFineractAccountFailed(String requestId, CreateCustomerRequest customer) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_FINERACT_VIRTUAL_ACCT_FAIL.getMessage();
            String message = "Create fineract account FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing createFineractAccountFailed events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.createFineractVirtualAccount(..)) && args(requestId,customer)")
    public void createFineractAccountSuccess(String requestId, CreateCustomerRequest customer) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_FINERACT_VIRTUAL_ACCT_SUCC.getMessage();
            String message = "Create fineract account SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(),code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing createFineractAccountSuccess events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.updateCustomerCredentials(..)) && args(updateCustomerCredentialsModel,requestId,request)")
    public void updateCustomerCredentialsInProgress(UpdateCustomerCredentialsModel updateCustomerCredentialsModel,String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_UPD_INPROG.getMessage();
            String message = "Update customer Credentials INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing updateCustomerDetailsInProgress events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.updateCustomerCredentials(..)) && args(updateCustomerCredentialsModel,requestId,request)")
    public void updateCustomerCredentialsFailed(UpdateCustomerCredentialsModel updateCustomerCredentialsModel,String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_UPD_FAIL.getMessage();
            String message = "Update customer details FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing updateCustomerDetailsFailed events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomer(..)) && args(updateCustomerCredentialsModel,requestId,request)")
    public void updateCustomerDetailsSuccess(UpdateCustomerCredentialsModel updateCustomerCredentialsModel,String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_UPD_SUCC.getMessage();
            String message = "Update customer details SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing updateCustomerDetailsSuccess events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    /*
    ******** PWMVP3-86 ******************
    Event logging changes for wrapper Api's
    */
    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.inititateDepositAllocation(..)) && args(depositAllocationRequestWrapperModel,requestId,request)")
    public void initiateDepositAllocationInProgress(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_DEPOSIT_ALLOCATION_INPROG.getMessage();
            String message = "Initiate Deposit Allocation INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateDepositAllocationInProgress events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.inititateDepositAllocation(..)) && args(depositAllocationRequestWrapperModel,requestId,request)")
    public void initiateDepositAllocationFailed(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_DEPOSIT_ALLOCATION_FAIL.getMessage();
            String message = "Initiate Deposit Allocation FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateDepositAllocationFailed events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.inititateDepositAllocation(..)) && args(depositAllocationRequestWrapperModel,requestId,request)")
    public void initiateDepositAllocationSuccess(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_DEPOSIT_ALLOCATION_SUCC.getMessage();
            String message = "Initiate Deposit Allocation SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateDepositAllocationSuccess events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateEmploymentVerification(..)) && args(employmentVerificationRequestWrapperModel,requestId,request)")
    public void initiateEmploymentVerificationInProgress(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_EMPLOYMENT_VERIFICATION_INPROG.getMessage();
            String message = "Initiate Employment Verification INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateEmploymentVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateEmploymentVerification(..)) && args(employmentVerificationRequestWrapperModel,requestId,request)")
    public void initiateEmploymentVerificationFailed(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_EMPLOYMENT_VERIFICATION_FAIL.getMessage();
            String message = "Initiate Employment Verification FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateEmploymentVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateEmploymentVerification(..)) && args(employmentVerificationRequestWrapperModel,requestId,request)")
    public void initiateEmploymentVerificationSuccess(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_EMPLOYMENT_VERIFICATION_SUCC.getMessage();
            String message = "Initiate Employment Verification SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateEmploymentVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIncomeVerification(..)) && args(incomeVerificationRequestWrapperModel,requestId,request)")
    public void initiateIncomeVerificationInProgress(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_INCOME_VERIFICATION_INPROG.getMessage();
            String message = "Initiate Income Verification INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIncomeVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIncomeVerification(..)) && args(incomeVerificationRequestWrapperModel,requestId,request)")
    public void initiateIncomeVerificationFailed(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_INCOME_VERIFICATION_FAIL.getMessage();
            String message = "Initiate Income Verification FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIncomeVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIncomeVerification(..)) && args(incomeVerificationRequestWrapperModel,requestId,request)")
    public void initiateIncomeVerificationSuccess(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_INCOME_VERIFICATION_SUCC.getMessage();
            String message = "Initiate Income Verification SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIncomeVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIdentityVerification(..)) && args(identityVerificationRequestWrapperModel,requestId,request)")
    public void initiateIdentityVerificationInProgress(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_IDENTITY_VERIFICATION_INPROG.getMessage();
            String message = "Initiate Identity Verification INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIdentityVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIdentityVerification(..)) && args(identityVerificationRequestWrapperModel,requestId,request)")
    public void initiateIdentityVerificationFailed(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_IDENTITY_VERIFICATION_FAIL.getMessage();
            String message = "Initiate Identity Verification FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIdentityVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIdentityVerification(..)) && args(identityVerificationRequestWrapperModel,requestId,request)")
    public void initiateIdentityVerificationSuccess(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_IDENTITY_VERIFICATION_SUCC.getMessage();
            String message = "Initiate Identity Verification SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIdentityVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.EmploymentVerificationWrapperAPIController.retryEmploymentVerification(..)) && args(requestId,empVerificationRequestDTO,request)")
    public void retryEmploymentVerificationInProgress(String requestId, WrapperRetryRequest empVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_EMPLOYMENT_VERIFICATION_RETRY_INPROG.getMessage();
            String message = "Initiate Retry Employment Verification INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryEmploymentVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.wrapperAPI.EmploymentVerificationWrapperAPIController.retryEmploymentVerification(..)) && args(requestId,empVerificationRequestDTO,request)")
    public void retryEmploymentVerificationFailed(String requestId, WrapperRetryRequest empVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_EMPLOYMENT_VERIFICATION_RETRY_FAIL.getMessage();
            String message = "Initiate Retry Employment Verification FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryEmploymentVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.wrapperAPI.EmploymentVerificationWrapperAPIController.retryEmploymentVerification(..)) && args(requestId,empVerificationRequestDTO,request)")
    public void retryEmploymentVerificationSuccess(String requestId, WrapperRetryRequest empVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_EMPLOYMENT_VERIFICATION_RETRY_SUCC.getMessage();
            String message = "Initiate Retry Employment Verification SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryEmploymentVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IdentityVerificationWrapperAPIController.retryIdentityVerification(..)) && args(requestId,identityVerificationRequestDTO,request)")
    public void retryIdentityVerificationInProgress(String requestId, WrapperRetryRequest identityVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_IDENTITY_VERIFICATION_RETRY_INPROG.getMessage();
            String message = "Initiate Retry Identity Verification INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIdentityVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IdentityVerificationWrapperAPIController.retryIdentityVerification(..)) && args(requestId,identityVerificationRequestDTO,request)")
    public void retryIdentityVerificationFailed(String requestId, WrapperRetryRequest identityVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_IDENTITY_VERIFICATION_RETRY_FAIL.getMessage();
            String message = "Initiate Retry Identity Verification FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIdentityVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IdentityVerificationWrapperAPIController.retryIdentityVerification(..)) && args(requestId,identityVerificationRequestDTO,request)")
    public void retryIdentityVerificationSuccess(String requestId, WrapperRetryRequest identityVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_IDENTITY_VERIFICATION_RETRY_SUCC.getMessage();
            String message = "Initiate Retry Identity Verification SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIdentityVerification events for requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IncomeVerificationWrapperAPIController.retryIncomeVerification(..)) && args(requestId,incomeVerificationRequestDTO,request)")
    public void retryIncomeVerificationInProgress(String requestId, WrapperRetryRequest incomeVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INCOME_VERIFICATION_RETRY_INPROG.getMessage();
            String message = "Initiate Retry Income Verification INPROGRESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIncomeVerification events for the requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.IN_PROGRESS,e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IncomeVerificationWrapperAPIController.retryIncomeVerification(..)) && args(requestId,incomeVerificationRequestDTO,request)")
    public void retryIncomeVerificationFailed(String requestId, WrapperRetryRequest incomeVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INCOME_VERIFICATION_RETRY_FAIL.getMessage();
            String message = "Initiate Retry Income Verification FAILED";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.FAILED);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIncomeVerification events for the requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.FAILED,e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IncomeVerificationWrapperAPIController.retryIncomeVerification(..)) && args(requestId,incomeVerificationRequestDTO,request)")
    public void retryIncomeVerificationSuccess(String requestId, WrapperRetryRequest incomeVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INCOME_VERIFICATION_RETRY_SUCC.getMessage();
            String message = "Initiate Retry Income Verification SUCCESS";
            borrowerEvent.triggerEvent(requestIdUtil.getDecodedRequestID(requestId).get(), code, message,SERVICE_NAME, ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIncomeVerification events for the requestId : {} : {} : {}",requestIdUtil.getDecodedRequestID(requestId).get(),ProgressLevel.SUCCESS,e);
        }
    }
}
