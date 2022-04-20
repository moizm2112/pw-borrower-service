package com.paywallet.userservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderInfo {
    
    private String providerName;
    private String providerCompanyId;
    private String pdSupported;
    private String percentSupported;
    private String amountSupported;
    private String fractionSupported;
    private String profileSupported;
    private String payoutsAllocated;
    private String payAllocacations;
    private String providerId;
    private String logo;
}
