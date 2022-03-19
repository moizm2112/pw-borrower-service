package com.paywallet.userservice.user.model.notification;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmailNotificationError {
    private EmailErrorDetail emailErrorDetail;
}
