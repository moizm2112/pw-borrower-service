package com.paywallet.userservice.user.event;

import com.paywallet.userservice.user.dto.EventDTO;
import com.paywallet.userservice.user.enums.ProgressLevel;
import com.paywallet.userservice.user.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BorrowerEvent {
    @Value("${event.logging.service.uri}")
    private String eventLoggingServiceUri;

    @Autowired
    CommonUtil commonUtil;

    @Value("${kafka.event.topic:paywallet.event-topic}")
    private String kafkaTopic;

    @Autowired
    KafkaTemplate<String,EventDTO> kafkaTemplate;

    public void triggerEvent(String requestId, String code, String message, String service, ProgressLevel progressLevel) {
        EventDTO eventDTO = commonUtil.prepareEvent(requestId, code, service, message, progressLevel);
        kafkaTemplate.send(kafkaTopic,eventDTO);
    }

    public void triggerEventWithAdditionalData(EventDTO eventDTO) {
        kafkaTemplate.send(kafkaTopic,eventDTO);
    }

}
