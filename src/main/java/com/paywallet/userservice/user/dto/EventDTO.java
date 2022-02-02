package com.paywallet.userservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String eventId;
    private String requestId;
    private String code;
    private String source;
    private String message;
    private String level;
    private Date time;
}
