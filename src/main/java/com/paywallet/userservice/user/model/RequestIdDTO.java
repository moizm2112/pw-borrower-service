package com.paywallet.userservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.paywallet.userservice.user.enums.FlowTypeEnum;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestIdDTO {
    private String employer;
    private String employerPWId;
    private String provider;
    private String identifyStatus;
    private String depositStatus;
    private String submitPD;
    private String allocationStatus;
    private String affordability;
    private String employmentStatus;
    private String incomeValidation;
    private String userId;
    private String clientTransactionId;
    private String taskId;
    private String payrollId;
    private String virtualAccountNumber;
    private String virtualAccountId;
    private String argyleAccountId;
    private String argyleUserId;
    private String depositTaskId;
    private String salaryAccountNumber;
    private String abaNumber;
    private int accountValidationAttempt;
    private List<String> identityCallbackUrls;
    private List<String> employmentCallbackUrls;
    private List<String> incomeCallbackUrls;
    private List<String> allocationCallbackUrls;
    private List<String> insufficientFundCallbackUrls;
    private List<String> notificationUrls;
    private boolean isDirectDepositAllocation;
    private List<FlowTypeEnum> flowType;
}
