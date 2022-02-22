package com.paywallet.userservice.user.util;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.enums.StatusEnum;
import com.paywallet.userservice.user.model.LinkServiceInfo;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.services.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPublisherUtil {

    @Autowired
    KafkaProducerService kafkaProducerService;

    public void publishLinkServiceInfo(RequestIdDetails requestIdDtls, CustomerDetails customerDetails,int installmentAmount) {
        try {
            LinkServiceInfo linkServiceInfo = LinkServiceInfo.builder()
                    .requestId(requestIdDtls.getRequestId())
                    .eventType(CommonEnum.CUSTOMER_CREATED.getMessage())
                    .lenderName(requestIdDtls.getClientName())
                    .phoneNumber(customerDetails.getPersonalProfile().getMobileNo())
                    .email(customerDetails.getPersonalProfile().getEmailId())
                    .employer(requestIdDtls.getEmployer())
                    .installmentAmount(String.valueOf(installmentAmount))
                    .payCycle(CommonEnum.PAY_CYCLE.getMessage()).build();
            StatusEnum statusEnum = kafkaProducerService.publishLinkServiceInfo(linkServiceInfo);
            log.info(" requestId : {}  publish status  : {} ", requestIdDtls.getRequestId(), statusEnum);
        } catch (Exception ex) {
            log.error(" Exception occurred while publishing LinkServiceInfo {}  request id :", ex, requestIdDtls.getRequestId());
        }
    }
}
