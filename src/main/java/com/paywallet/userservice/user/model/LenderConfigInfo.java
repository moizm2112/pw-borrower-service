package com.paywallet.userservice.user.model;

import org.springframework.stereotype.Component;

import com.paywallet.userservice.user.enums.StateStatus;

import lombok.Data;

@Component
@Data
public class LenderConfigInfo {

    private String lenderId;
    private String lenderName;
    private StateStatus publishIdentityInfo;
    private StateStatus publishEmploymentInfo;
    private StateStatus publishIncomeInfo;
    private StateStatus validateSalaryAccountOwnership;
    private StateStatus validateAffordabilityCheck;
    private StateStatus invokeAndPublishDepositAllocation;

}
