package com.paywallet.userservice.user.util;

import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.enums.StatusEnum;
import com.paywallet.userservice.user.model.LinkServiceInfo;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.services.KafkaProducerService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaPublisherUtil {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Autowired
    CommonUtil commonUtil;

    public void publishLinkServiceInfo(RequestIdDetails requestIdDtls, CustomerDetails customerDetails, double installmentAmount, FlowTypeEnum flowType,
    		boolean isEmployerPdSupported) {
        try {
        	boolean isDepositAllocation = false;
        	if(flowType.name().equals(FlowTypeEnum.DEPOSIT_ALLOCATION))
        		isDepositAllocation = true;
            LinkServiceInfo linkServiceInfo = LinkServiceInfo.builder()
                    .requestId(requestIdDtls.getRequestId())
                    .eventType(CommonEnum.CUSTOMER_CREATED.getMessage())
                    .lenderName(requestIdDtls.getClientName())
                    .phoneNumber(customerDetails.getPersonalProfile().getCellPhone())
                    .email(customerDetails.getPersonalProfile().getEmailId())
                    .employer(requestIdDtls.getEmployer())
                    .installmentAmount(commonUtil.getFormattedAmount(installmentAmount))
                    .payCycle(CommonEnum.PAY_CYCLE.getMessage())
                    .flowType(flowType)
                    .isDirectDepositAllocation(isDepositAllocation)
                    .isPdSupported(isEmployerPdSupported)
                    .build();
            StatusEnum statusEnum = kafkaProducerService.publishLinkServiceInfo(linkServiceInfo);
            log.info(" requestId : {}  publish status  : {} ", requestIdDtls.getRequestId(), statusEnum);
        } catch (Exception ex) {
            Sentry.captureException(ex);
            log.error(" Exception occurred while publishing LinkServiceInfo {}  request id :", ex, requestIdDtls.getRequestId());
        }
    }
    
    public void publishLinkServiceInfo(RequestIdDetails requestIdDtls, CustomerDetails customerDetails, FlowTypeEnum flowType) {
        try {
            LinkServiceInfo linkServiceInfo = LinkServiceInfo.builder()
                    .requestId(requestIdDtls.getRequestId())
                    .eventType(CommonEnum.CUSTOMER_CREATED.getMessage())
                    .lenderName(requestIdDtls.getClientName())
                    .phoneNumber(customerDetails.getPersonalProfile().getCellPhone())
                    .email(customerDetails.getPersonalProfile().getEmailId())
                    .employer(requestIdDtls.getEmployer())
                    .payCycle(CommonEnum.PAY_CYCLE.getMessage())
                    .flowType(flowType)
                    .linkFlag(true)
                    .build();
            StatusEnum statusEnum = kafkaProducerService.publishLinkServiceInfo(linkServiceInfo);
            log.info(" requestId : {}  publish status  : {} ", requestIdDtls.getRequestId(), statusEnum);
        } catch (Exception ex) {
            Sentry.captureException(ex);
            log.error(" Exception occurred while publishing LinkServiceInfo {}  request id :", ex, requestIdDtls.getRequestId());
        }
    }
}
