package com.paywallet.userservice.user.model;

import com.paywallet.userservice.user.enums.StateStatus;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class StateControllerInfo {

    private String lenderId;
    private String lenderName;
    private StateStatus publishIdentityInfo;
    private StateStatus publishEmploymentInfo;
    private StateStatus publishIncomeInfo;
    private StateStatus validateSalaryAccountOwnership;
    private StateStatus ValidateAffordabilityCheck;
    private StateStatus invokeAndPublishDepositAllocation;
}
