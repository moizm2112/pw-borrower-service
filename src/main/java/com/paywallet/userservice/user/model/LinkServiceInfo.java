package com.paywallet.userservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkServiceInfo {

    private String requestId;
    private String eventType;
    private String lenderName;
    private String phoneNumber;
    private String email;
    private String employer;
    private String installmentAmount;
    private String payCycle;

}
