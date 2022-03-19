package com.paywallet.userservice.user.model.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmailErrorDetail {
    private String email;
    private String event;
    private String reason;
    @JsonProperty("sg_event_id")
    private String sgEventId;
    @JsonProperty("sg_message_id")
    private String sgMessageId;
    @JsonProperty("sg_template_id")
    private String sgTemplateId;
    @JsonProperty("sg_template_name")
    private String sgTemplateName;
    @JsonProperty("smtp-id")
    private String smtpId;
    private String timestamp;
    private String tls;
    private String type;
}
