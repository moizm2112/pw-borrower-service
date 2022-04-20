package com.paywallet.userservice.user.dto;

import com.paywallet.userservice.user.enums.ProgressLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

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
    private ProgressLevel level;
    private String dateTime;
    private AdditionalInfoDTO additionalInfoDTO;
}
