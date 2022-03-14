package com.paywallet.userservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerSearchDetailsDTO {
    private String id;
    private String employerName;
    private String logoUrl;
    private String providerId;
    private String payrollProviderUsed;
}
