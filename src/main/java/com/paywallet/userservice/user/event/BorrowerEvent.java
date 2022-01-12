package com.paywallet.userservice.user.event;

import com.paywallet.userservice.user.dto.EventDTO;
import com.paywallet.userservice.user.enums.ProgressLevel;
import com.paywallet.userservice.user.model.RequestIdResponseDTO;
import com.paywallet.userservice.user.util.CustomerServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Component
public class BorrowerEvent {
    @Value("${event.logging.service.uri}")
    private String eventLoggingServiceUri;

    CustomerServiceUtil customerServiceUtil;

    public CompletableFuture triggerEvent(String requestId, String code, String message, String service, ProgressLevel progressLevel) {
        EventDTO eventDTO = customerServiceUtil.prepareEvent(requestId, code, service, message, progressLevel.name());
        RestTemplate restTemplate = new RestTemplate();
        return CompletableFuture.supplyAsync(()->restTemplate
                .exchange(eventLoggingServiceUri, HttpMethod.POST, new HttpEntity(eventDTO), String.class)
                .getBody());
    }
}
