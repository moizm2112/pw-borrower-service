package com.paywallet.userservice.user.model.notification;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

@Data
@Component
public class Notification {
    @Id
    private String id;
    private NotificationType type;
    private String templateId;
    private String templateBody;
    private String from;
    private String to;
    private String subject;
    private String body;
    private String createdAt;
    private DeliveryStatus deliveryStatus;
    private String requestor;
    private String requestId;
    private String trackId;
    private String requestType;
    private NotificationError notificationError;

}
