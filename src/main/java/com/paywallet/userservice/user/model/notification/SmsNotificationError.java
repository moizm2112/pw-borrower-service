package com.paywallet.userservice.user.model.notification;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
public class SmsNotificationError {
    private Map<String, String> values;
}
