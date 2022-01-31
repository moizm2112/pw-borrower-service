package com.paywallet.userservice.user.aspect;

import com.paywallet.userservice.user.enums.BorrowerEventEnum;
import com.paywallet.userservice.user.enums.ProgressLevel;
import com.paywallet.userservice.user.event.BorrowerEvent;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.LyonsAPIRequestDTO;
import com.paywallet.userservice.user.model.UpdateCustomerRequestDTO;
import com.paywallet.userservice.user.model.ValidateAccountRequest;
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

    private static final String SERVICE_NAME = "Borrower";

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.createCustomer(..)) && args(customer,apiKey,request)")
    public void customerCreateInProgress(CreateCustomerRequest customer, String apiKey, HttpServletRequest request) {
        try{
            String requestId = apiKey;
            String code = BorrowerEventEnum.UMS_CUST_CREATE_INPROG.getMessage();
            String message = "Customer create INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing customerCreateInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.createCustomer(..)) && args(customer,apiKey,request)")
    public void customerCreateFailed(CreateCustomerRequest customer, String apiKey, HttpServletRequest request) {
        try{
            String requestId = apiKey;
            String code = BorrowerEventEnum.UMS_CUST_CREATE_FAIL.getMessage();
            String message = "Customer create FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing customerCreateFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.createCustomer(..)) && args(customer,apiKey,request)")
    public void customerCreateSuccess(CreateCustomerRequest customer, String apiKey, HttpServletRequest request) {
        try{
            String requestId = apiKey;
            String code = BorrowerEventEnum.UMS_CUST_CREATE_SUCC.getMessage();
            String message = "Customer create SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing customerCreateSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.fetchrequestIdDetails(..)) && args(requestId,identifyProviderServiceUri,restTemplate)")
    public void requestIdCreateInProgress(String requestId, String identifyProviderServiceUri, RestTemplate restTemplate) {
        try{
            String code = BorrowerEventEnum.UMS_GET_REQ_DETAIL_INPROG.getMessage();
            String message = "Request id details create INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing requestIdCreateInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.fetchrequestIdDetails(..)) && args(requestId,identifyProviderServiceUri,restTemplate)")
    public void requestIdCreateFailed(String requestId, String identifyProviderServiceUri, RestTemplate restTemplate) {
        try{
            String code = BorrowerEventEnum.UMS_GET_REQ_DETAIL_FAIL.getMessage();
            String message = "Request id details create FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing requestIdCreateFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.fetchrequestIdDetails(..)) && args(requestId,identifyProviderServiceUri,restTemplate)")
    public void requestIdCreateSuccess(String requestId, String identifyProviderServiceUri, RestTemplate restTemplate) {
        try{
            String code = BorrowerEventEnum.UMS_GET_REQ_DETAIL_SUCC.getMessage();
            String message = "Request id details create SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing requestIdCreateSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.updateRequestIdDetails(..)) && args(requestId,customerId,virtualAccountNumber,identifyProviderServiceUri,restTemplate,customerRequest)")
    public void requestIdUpdateInProgress(String requestId, String customerId, String virtualAccountNumber,
                                          String identifyProviderServiceUri, RestTemplate restTemplate, CreateCustomerRequest customerRequest) {
        try{
            String code = BorrowerEventEnum.UMS_UPD_REQ_DETAIL_INPROG.getMessage();
            String message = "Request id details update INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing requestIdUpdateInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.updateRequestIdDetails(..)) && args(requestId,customerId,virtualAccountNumber,identifyProviderServiceUri,restTemplate,customerRequest)")
    public void requestIdUpdateFailed(String requestId, String customerId, String virtualAccountNumber,
                                      String identifyProviderServiceUri, RestTemplate restTemplate, CreateCustomerRequest customerRequest) {
        try{
            String code = BorrowerEventEnum.UMS_UPD_REQ_DETAIL_FAIL.getMessage();
            String message = "Request id details update FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing requestIdUpdateFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.updateRequestIdDetails(..)) && args(requestId,customerId,virtualAccountNumber,identifyProviderServiceUri,restTemplate,customerRequest)")
    public void requestIdUpdateSuccess(String requestId, String customerId, String virtualAccountNumber,
                                       String identifyProviderServiceUri, RestTemplate restTemplate, CreateCustomerRequest customerRequest) {
        try{
            String code = BorrowerEventEnum.UMS_UPD_REQ_DETAIL_SUCC.getMessage();
            String message = "Request id update details SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing requestIdUpdateSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.createFineractVirtualAccount(..)) && args(requestId,customer)")
    public void createFineractAccountInProgress(String requestId, CreateCustomerRequest customer) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_FINERACT_VIRTUAL_ACCT_INPROG.getMessage();
            String message = "Create fineract account INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing createFineractAccountInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.createFineractVirtualAccount(..)) && args(requestId,customer)")
    public void createFineractAccountFailed(String requestId, CreateCustomerRequest customer) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_FINERACT_VIRTUAL_ACCT_FAIL.getMessage();
            String message = "Create fineract account FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing createFineractAccountFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.CustomerServiceHelper.createFineractVirtualAccount(..)) && args(requestId,customer)")
    public void createFineractAccountSuccess(String requestId, CreateCustomerRequest customer) {
        try{
            String code = BorrowerEventEnum.UMS_CUST_CREATE_FINERACT_VIRTUAL_ACCT_SUCC.getMessage();
            String message = "Create fineract account SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing createFineractAccountSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomerByMobile(..)) && args(mobileNo,request)")
    public void getCustomerDataByMobileInProgress(String mobileNo, HttpServletRequest request) {
        try{
            String requestId = mobileNo;
            String code = BorrowerEventEnum.UMS_CUST_GET_BY_MOBILE_INPROG.getMessage();
            String message = "Get customer data by mobile INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing getCustomerDataByMobileInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomerByMobile(..)) && args(mobileNo,request)")
    public void getCustomerDataByMobileFailed(String mobileNo, HttpServletRequest request) {
        try{
            String requestId = mobileNo;
            String code = BorrowerEventEnum.UMS_CUST_GET_BY_MOBILE_FAIL.getMessage();
            String message = "Get customer data by mobile FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing getCustomerDataByMobileFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomerByMobile(..)) && args(mobileNo,request)")
    public void getCustomerDataByMobileSuccess(String mobileNo, HttpServletRequest request) {
        try{
            String requestId = mobileNo;
            String code = BorrowerEventEnum.UMS_CUST_GET_BY_MOBILE_SUCC.getMessage();
            String message = "Get customer data by mobile SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing getCustomerDataByMobileSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomer(..)) && args(customerId,request)")
    public void getCustomerDataInProgress(String customerId, HttpServletRequest request) {
        try{
            String requestId = customerId;
            String code = BorrowerEventEnum.UMS_CUST_GET_INPROG.getMessage();
            String message = "Get customer Data INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing getCustomerDataInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomer(..)) && args(customerId,request)")
    public void getCustomerDataFailed(String customerId, HttpServletRequest request) {
        try{
            String requestId = customerId;
            String code = BorrowerEventEnum.UMS_CUST_GET_FAIL.getMessage();
            String message = "Get customer Data FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing getCustomerDataFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomer(..)) && args(customerId,request)")
    public void getCustomerDataSuccess(String customerId, HttpServletRequest request) {
        try{
            String requestId = customerId;
            String code = BorrowerEventEnum.UMS_CUST_GET_SUCC.getMessage();
            String message = "Get customer Data SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing getCustomerDataSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getAccountDetails(..)) && args(mobileNo,request)")
    public void getAccountDetailsProgress(String mobileNo, HttpServletRequest request) {
        try{
            String requestId = mobileNo;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_GET_BY_MOBILE_INPROG.getMessage();
            String message = "Get account details INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing getAccountDetailsProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.getAccountDetails(..)) && args(mobileNo,request)")
    public void getAccountDetailsFailed(String mobileNo, HttpServletRequest request) {
        try{
            String requestId = mobileNo;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_GET_BY_MOBILE_FAIL.getMessage();
            String message = "Get account details FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing getAccountDetailsFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.getAccountDetails(..)) && args(mobileNo,request)")
    public void getAccountDetailsSuccess(String mobileNo, HttpServletRequest request) {
        try{
            String requestId = mobileNo;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_GET_BY_MOBILE_SUCC.getMessage();
            String message = "Get account details SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing getAccountDetailsSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.validateAccount(..)) && args(validateAccountRequest,request)")
    public void validateCustomerDetailsProgress(ValidateAccountRequest validateAccountRequest,
                                          HttpServletRequest request) {
        try{
            String requestId = validateAccountRequest.getMobileNo();
            String code = BorrowerEventEnum.UMS_CUST_ACCT_VAL_INPROG.getMessage();
            String message = "Validate account details INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing validateCustomerDetailsProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.validateAccount(..)) && args(validateAccountRequest,request)")
    public void validateCustomerDetailsFailed(ValidateAccountRequest validateAccountRequest,
                                        HttpServletRequest request) {
        try{
            String requestId = validateAccountRequest.getMobileNo();
            String code = BorrowerEventEnum.UMS_CUST_ACCT_VAL_FAIL.getMessage();
            String message = "Validate customer account details FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing validateCustomerDetailsFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.validateAccount(..)) && args(validateAccountRequest,request)")
    public void validateCustomerAccountDetailsSuccess(ValidateAccountRequest validateAccountRequest,
                                         HttpServletRequest request) {
        try{
            String requestId = validateAccountRequest.getMobileNo();
            String code = BorrowerEventEnum.UMS_CUST_ACCT_VAL_SUCC.getMessage();
            String message = "Validate customer account details SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing validateCustomerAccountDetailsSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.services.LyonsService.checkAccountOwnership(..)) && args(apiRequest)")
    public void lyonsApiCallInProgress(LyonsAPIRequestDTO apiRequest) {
        try{
            String requestId = apiRequest.getAccountNumber();
            String code = BorrowerEventEnum.UMS_CUST_LYON_ACCT_VAL_INPROG.getMessage();
            String message = "Lyons Api call INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing lyonsApiCallInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.LyonsService.checkAccountOwnership(..)) && args(apiRequest)")
    public void lyonsApiCallFailed(LyonsAPIRequestDTO apiRequest) {
        try{
            String requestId = apiRequest.getAccountNumber();
            String code = BorrowerEventEnum.UMS_CUST_LYON_ACCT_VAL_FAIL.getMessage();
            String message = "Lyons Api call FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing lyonsApiCallFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.LyonsService.checkAccountOwnership(..)) && args(apiRequest)")
    public void lyonsApiCallSuccess(LyonsAPIRequestDTO apiRequest) {
        try{
            String requestId = apiRequest.getAccountNumber();
            String code = BorrowerEventEnum.UMS_CUST_LYON_ACCT_VAL_SUCC.getMessage();
            String message = "Lyons Api call SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing lyonsApiCallSuccess events : {}",e);
        }
    }

    //
    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomer(..)) && args(updateCustomerRequest,request)")
    public void updateCustomerDetailsInProgress(UpdateCustomerRequestDTO updateCustomerRequest, HttpServletRequest request) {
        try{
            String requestId = updateCustomerRequest.getMobileNo();
            String code = BorrowerEventEnum.UMS_CUST_UPD_INPROG.getMessage();
            String message = "Update customer details INPROGRESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.IN_PROGRESS);
        } catch (Throwable e) {
            log.error("Error while publishing updateCustomerDetailsInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomer(..)) && args(updateCustomerRequest,request)")
    public void updateCustomerDetailsFailed(UpdateCustomerRequestDTO updateCustomerRequest, HttpServletRequest request) {
        try{
            String requestId = updateCustomerRequest.getMobileNo();
            String code = BorrowerEventEnum.UMS_CUST_UPD_FAIL.getMessage();
            String message = "Update customer details FAILED";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.FAILED);
        } catch (Throwable e) {
            log.error("Error while publishing updateCustomerDetailsFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomer(..)) && args(updateCustomerRequest,request)")
    public void updateCustomerDetailsSuccess(UpdateCustomerRequestDTO updateCustomerRequest, HttpServletRequest request) {
        try{
            String requestId = updateCustomerRequest.getMobileNo();
            String code = BorrowerEventEnum.UMS_CUST_UPD_SUCC.getMessage();
            String message = "Update customer details SUCCESS";
            borrowerEvent.triggerEvent(requestId,code,message,SERVICE_NAME,ProgressLevel.SUCCESS);
        } catch (Throwable e) {
            log.error("Error while publishing updateCustomerDetailsSuccess events : {}",e);
        }
    }
}
