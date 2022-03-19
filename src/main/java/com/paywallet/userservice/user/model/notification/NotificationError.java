package com.paywallet.userservice.user.model.notification;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class NotificationError {
    private SmsNotificationError smsNotificationError;
    private EmailNotificationError emailNotificationError;
}
