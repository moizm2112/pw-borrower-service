package com.paywallet.userservice.user.aspect;

import com.paywallet.userservice.user.dto.AdditionalInfoDTO;
import com.paywallet.userservice.user.dto.EventDTO;
import com.paywallet.userservice.user.enums.BorrowerEventEnum;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.enums.ProgressLevel;
import com.paywallet.userservice.user.event.BorrowerEvent;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.LyonsAPIRequestDTO;
import com.paywallet.userservice.user.model.UpdateCustomerRequestDTO;
import com.paywallet.userservice.user.model.ValidateAccountRequest;
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
            log.error("Error while publishing customerCreateInProgress events : {}",e);
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
            log.error("Error while publishing customerCreateFailed events : {}",e);
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
            log.error("Error while publishing customerCreateSuccess events : {}",e);
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
            log.error("Error while publishing requestIdCreateInProgress events : {}",e);
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
            log.error("Error while publishing requestIdCreateFailed events : {}",e);
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
            log.error("Error while publishing requestIdCreateSuccess events : {}",e);
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
            log.error("Error while publishing requestIdUpdateInProgress events : {}",e);
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
            log.error("Error while publishing requestIdUpdateFailed events : {}",e);
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
            log.error("Error while publishing requestIdUpdateSuccess events : {}",e);
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
            log.error("Error while publishing createFineractAccountInProgress events : {}",e);
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
            log.error("Error while publishing createFineractAccountFailed events : {}",e);
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
            log.error("Error while publishing createFineractAccountSuccess events : {}",e);
        }
    }

    /* @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomerByMobile(..)) && args(cellPhone,request)")
    public void getCustomerDataByMobileInProgress(String cellPhone, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_GET_BY_MOBILE_INPROG.getMessage();
            String message = "Get customer data by mobile INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(cellPhone);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getCustomerDataByMobileInProgress events : {}",e);
        }
    }

   @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomerByMobile(..)) && args(cellPhone,request)")
    public void getCustomerDataByMobileFailed(String cellPhone, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_GET_BY_MOBILE_FAIL.getMessage();
            String message = "Get customer data by mobile FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(cellPhone);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getCustomerDataByMobileFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomerByMobile(..)) && args(cellPhone,request)")
    public void getCustomerDataByMobileSuccess(String cellPhone, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_GET_BY_MOBILE_SUCC.getMessage();
            String message = "Get customer data by mobile SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(cellPhone);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getCustomerDataByMobileSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomer(..)) && args(customerId,request)")
    public void getCustomerDataInProgress(String customerId, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_GET_INPROG.getMessage();
            String message = "Get customer Data INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCustomerId(customerId);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getCustomerDataInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomer(..)) && args(customerId,request)")
    public void getCustomerDataFailed(String customerId, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_GET_FAIL.getMessage();
            String message = "Get customer Data FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCustomerId(customerId);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getCustomerDataFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.getCustomer(..)) && args(customerId,request)")
    public void getCustomerDataSuccess(String customerId, HttpServletRequest request) {
        try{
            String requestId = customerId;
            String code = BorrowerEventEnum.UMS_CUST_GET_SUCC.getMessage();
            String message = "Get customer Data SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCustomerId(customerId);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getCustomerDataSuccess events : {}",e);
        }
    }
*/
    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.getAccountDetails(..)) && args(cellPhone,request)")
    public void getAccountDetailsProgress(String cellPhone, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_GET_BY_MOBILE_INPROG.getMessage();
            String message = "Get account details INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(cellPhone);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getAccountDetailsProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.getAccountDetails(..)) && args(cellPhone,request)")
    public void getAccountDetailsFailed(String cellPhone, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_GET_BY_MOBILE_FAIL.getMessage();
            String message = "Get account details FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(cellPhone);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getAccountDetailsFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.getAccountDetails(..)) && args(cellPhone,request)")
    public void getAccountDetailsSuccess(String cellPhone, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_GET_BY_MOBILE_SUCC.getMessage();
            String message = "Get account details SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(cellPhone);
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing getAccountDetailsSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.validateAccount(..)) && args(validateAccountRequest,request)")
    public void validateCustomerDetailsProgress(ValidateAccountRequest validateAccountRequest,
                                          HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_VAL_INPROG.getMessage();
            String message = "Validate account details INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(validateAccountRequest.getCellPhone());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing validateCustomerDetailsProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.validateAccount(..)) && args(validateAccountRequest,request)")
    public void validateCustomerDetailsFailed(ValidateAccountRequest validateAccountRequest,
                                        HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_VAL_FAIL.getMessage();
            String message = "Validate customer account details FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(validateAccountRequest.getCellPhone());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing validateCustomerDetailsFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.validateAccount(..)) && args(validateAccountRequest,request)")
    public void validateCustomerAccountDetailsSuccess(ValidateAccountRequest validateAccountRequest,
                                         HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_ACCT_VAL_SUCC.getMessage();
            String message = "Validate customer account details SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(validateAccountRequest.getCellPhone());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing validateCustomerAccountDetailsSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.services.LyonsService.checkAccountOwnership(..)) && args(apiRequest)")
    public void lyonsApiCallInProgress(LyonsAPIRequestDTO apiRequest) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_LYON_ACCT_VAL_INPROG.getMessage();
            String message = "Lyons Api call INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setSalaryAccountNumber(apiRequest.getAccountNumber());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing lyonsApiCallInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.services.LyonsService.checkAccountOwnership(..)) && args(apiRequest)")
    public void lyonsApiCallFailed(LyonsAPIRequestDTO apiRequest) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_LYON_ACCT_VAL_FAIL.getMessage();
            String message = "Lyons Api call FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setSalaryAccountNumber(apiRequest.getAccountNumber());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing lyonsApiCallFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.services.LyonsService.checkAccountOwnership(..)) && args(apiRequest)")
    public void lyonsApiCallSuccess(LyonsAPIRequestDTO apiRequest) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_LYON_ACCT_VAL_SUCC.getMessage();
            String message = "Lyons Api call SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setSalaryAccountNumber(apiRequest.getAccountNumber());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing lyonsApiCallSuccess events : {}",e);
        }
    }

    //
    @Before("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomer(..)) && args(updateCustomerRequest,request)")
    public void updateCustomerDetailsInProgress(UpdateCustomerRequestDTO updateCustomerRequest, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_UPD_INPROG.getMessage();
            String message = "Update customer details INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(updateCustomerRequest.getCellPhone());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing updateCustomerDetailsInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomer(..)) && args(updateCustomerRequest,request)")
    public void updateCustomerDetailsFailed(UpdateCustomerRequestDTO updateCustomerRequest, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_UPD_FAIL.getMessage();
            String message = "Update customer details FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(updateCustomerRequest.getCellPhone());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing updateCustomerDetailsFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerController.updateCustomer(..)) && args(updateCustomerRequest,request)")
    public void updateCustomerDetailsSuccess(UpdateCustomerRequestDTO updateCustomerRequest, HttpServletRequest request) {
        try{
            String requestId = null;
            String code = BorrowerEventEnum.UMS_CUST_UPD_SUCC.getMessage();
            String message = "Update customer details SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            AdditionalInfoDTO additionalInfoDTO= new AdditionalInfoDTO();
            additionalInfoDTO.setCellPhone(updateCustomerRequest.getCellPhone());
            eventDTO.setAdditionalInfoDTO(additionalInfoDTO);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing updateCustomerDetailsSuccess events : {}",e);
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
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateDepositAllocationInProgress events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.inititateDepositAllocation(..)) && args(depositAllocationRequestWrapperModel,requestId,request)")
    public void initiateDepositAllocationFailed(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_DEPOSIT_ALLOCATION_FAIL.getMessage();
            String message = "Initiate Deposit Allocation FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateDepositAllocationFailed events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.inititateDepositAllocation(..)) && args(depositAllocationRequestWrapperModel,requestId,request)")
    public void initiateDepositAllocationSuccess(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_DEPOSIT_ALLOCATION_SUCC.getMessage();
            String message = "Initiate Deposit Allocation SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateDepositAllocationSuccess events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateEmploymentVerification(..)) && args(employmentVerificationRequestWrapperModel,requestId,request)")
    public void initiateEmploymentVerificationInProgress(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_EMPLOYMENT_VERIFICATION_INPROG.getMessage();
            String message = "Initiate Employment Verification INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateEmploymentVerification events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateEmploymentVerification(..)) && args(employmentVerificationRequestWrapperModel,requestId,request)")
    public void initiateEmploymentVerificationFailed(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_EMPLOYMENT_VERIFICATION_FAIL.getMessage();
            String message = "Initiate Employment Verification FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateEmploymentVerification events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateEmploymentVerification(..)) && args(employmentVerificationRequestWrapperModel,requestId,request)")
    public void initiateEmploymentVerificationSuccess(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_EMPLOYMENT_VERIFICATION_SUCC.getMessage();
            String message = "Initiate Employment Verification SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateEmploymentVerification events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIncomeVerification(..)) && args(incomeVerificationRequestWrapperModel,requestId,request)")
    public void initiateIncomeVerificationInProgress(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_INCOME_VERIFICATION_INPROG.getMessage();
            String message = "Initiate Income Verification INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIncomeVerification events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIncomeVerification(..)) && args(incomeVerificationRequestWrapperModel,requestId,request)")
    public void initiateIncomeVerificationFailed(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_INCOME_VERIFICATION_FAIL.getMessage();
            String message = "Initiate Income Verification FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIncomeVerification events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIncomeVerification(..)) && args(incomeVerificationRequestWrapperModel,requestId,request)")
    public void initiateIncomeVerificationSuccess(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_INCOME_VERIFICATION_SUCC.getMessage();
            String message = "Initiate Income Verification SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIncomeVerification events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIdentityVerification(..)) && args(identityVerificationRequestWrapperModel,requestId,request)")
    public void initiateIdentityVerificationInProgress(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_IDENTITY_VERIFICATION_INPROG.getMessage();
            String message = "Initiate Identity Verification INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIdentityVerification events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIdentityVerification(..)) && args(identityVerificationRequestWrapperModel,requestId,request)")
    public void initiateIdentityVerificationFailed(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel,String requestId,HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_IDENTITY_VERIFICATION_FAIL.getMessage();
            String message = "Initiate Identity Verification FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIdentityVerification events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.CustomerWrapperApiController.initiateIdentityVerification(..)) && args(identityVerificationRequestWrapperModel,requestId,request)")
    public void initiateIdentityVerificationSuccess(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel, String requestId, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INITIATE_IDENTITY_VERIFICATION_SUCC.getMessage();
            String message = "Initiate Identity Verification SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing initiateIdentityVerification events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.EmploymentVerificationWrapperAPIController.retryEmploymentVerification(..)) && args(requestId,empVerificationRequestDTO,request)")
    public void retryEmploymentVerificationInProgress(String requestId, WrapperRetryRequest empVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_EMPLOYMENT_VERIFICATION_RETRY_INPROG.getMessage();
            String message = "Initiate Retry Employment Verification INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryEmploymentVerification events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.wrapperAPI.EmploymentVerificationWrapperAPIController.retryEmploymentVerification(..)) && args(requestId,empVerificationRequestDTO,request)")
    public void retryEmploymentVerificationFailed(String requestId, WrapperRetryRequest empVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_EMPLOYMENT_VERIFICATION_RETRY_FAIL.getMessage();
            String message = "Initiate Retry Employment Verification FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryEmploymentVerification events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.wrapperAPI.EmploymentVerificationWrapperAPIController.retryEmploymentVerification(..)) && args(requestId,empVerificationRequestDTO,request)")
    public void retryEmploymentVerificationSuccess(String requestId, WrapperRetryRequest empVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_EMPLOYMENT_VERIFICATION_RETRY_SUCC.getMessage();
            String message = "Initiate Retry Employment Verification SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryEmploymentVerification events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IdentityVerificationWrapperAPIController.retryIdentityVerification(..)) && args(requestId,identityVerificationRequestDTO,request)")
    public void retryIdentityVerificationInProgress(String requestId, WrapperRetryRequest identityVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_IDENTITY_VERIFICATION_RETRY_INPROG.getMessage();
            String message = "Initiate Retry Identity Verification INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIdentityVerification events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IdentityVerificationWrapperAPIController.retryIdentityVerification(..)) && args(requestId,identityVerificationRequestDTO,request)")
    public void retryIdentityVerificationFailed(String requestId, WrapperRetryRequest identityVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_IDENTITY_VERIFICATION_RETRY_FAIL.getMessage();
            String message = "Initiate Retry Identity Verification FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIdentityVerification events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IdentityVerificationWrapperAPIController.retryIdentityVerification(..)) && args(requestId,identityVerificationRequestDTO,request)")
    public void retryIdentityVerificationSuccess(String requestId, WrapperRetryRequest identityVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_IDENTITY_VERIFICATION_RETRY_SUCC.getMessage();
            String message = "Initiate Retry Identity Verification SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIdentityVerification events : {}",e);
        }
    }

    @Before("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IncomeVerificationWrapperAPIController.retryIncomeVerification(..)) && args(requestId,incomeVerificationRequestDTO,request)")
    public void retryIncomeVerificationInProgress(String requestId, WrapperRetryRequest incomeVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INCOME_VERIFICATION_RETRY_INPROG.getMessage();
            String message = "Initiate Retry Income Verification INPROGRESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.IN_PROGRESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIncomeVerification events : {}",e);
        }
    }

    @AfterThrowing("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IncomeVerificationWrapperAPIController.retryIncomeVerification(..)) && args(requestId,incomeVerificationRequestDTO,request)")
    public void retryIncomeVerificationFailed(String requestId, WrapperRetryRequest incomeVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INCOME_VERIFICATION_RETRY_FAIL.getMessage();
            String message = "Initiate Retry Income Verification FAILED";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.FAILED);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIncomeVerification events : {}",e);
        }
    }

    @AfterReturning("execution(* com.paywallet.userservice.user.controller.wrapperAPI.IncomeVerificationWrapperAPIController.retryIncomeVerification(..)) && args(requestId,incomeVerificationRequestDTO,request)")
    public void retryIncomeVerificationSuccess(String requestId, WrapperRetryRequest incomeVerificationRequestDTO, HttpServletRequest request) {
        try{
            String code = BorrowerEventEnum.UMS_INCOME_VERIFICATION_RETRY_SUCC.getMessage();
            String message = "Initiate Retry Income Verification SUCCESS";
            EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, SERVICE_NAME, message, ProgressLevel.SUCCESS);
            borrowerEvent.triggerEventWithAdditionalData(eventDTO);
        } catch (Throwable e) {
            Sentry.captureException(e);
            log.error("Error while publishing retryIncomeVerification events : {}",e);
        }
    }
}
