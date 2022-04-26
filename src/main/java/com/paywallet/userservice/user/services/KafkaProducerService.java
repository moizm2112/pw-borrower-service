package com.paywallet.userservice.user.services;

import com.paywallet.userservice.user.enums.StatusEnum;
import com.paywallet.userservice.user.exception.KafkaProducerException;
import com.paywallet.userservice.user.model.LinkServiceInfo;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RefreshScope
public class KafkaProducerService {

    @Autowired
    KafkaTemplate<String, LinkServiceInfo> kafkaTemplate;

    @Value("${link.service.topic}")
    private String linkServiceTopic;

    public StatusEnum publishLinkServiceInfo(LinkServiceInfo linkServiceInfo) throws KafkaProducerException {
        try {
            SendResult<String, LinkServiceInfo> producerRes = kafkaTemplate.send(linkServiceTopic, linkServiceInfo).get(10000, TimeUnit.MILLISECONDS);
            return StatusEnum.SUCCESS;
        } catch (Exception ex) {
            Sentry.captureException(ex);
            log.error(" Error while publishing the topic : {}  Request ID : {} ", ex, linkServiceInfo.getRequestId());
            throw new KafkaProducerException(ex.getMessage());
        }
    }

}
