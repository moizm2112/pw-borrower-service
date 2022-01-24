package com.paywallet.userservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import org.springframework.stereotype.Component;

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
    private String argyleAccountId;
    private String argyleUserId;
    private List<String> identityCallbackUrl;
    private List<String> employmentCallbackUrl;
    private List<String> incomeCallbackUrl;
    private List<String> allocationCallbackUrl;
    private List<String> insufficientFundCallbackUrl;
}
