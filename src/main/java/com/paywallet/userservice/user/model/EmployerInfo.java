package com.paywallet.userservice.user.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerInfo {

    private String paywalletCompanyId;
    private String paywalletCompanyName;
    private List<ProviderInfo> providerInfo;

}