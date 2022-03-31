package com.paywallet.userservice.user.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.paywallet.userservice.user.enums.FlowTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include. NON_NULL)
public class RequestIdDetails {
   
    private String requestId;
    private Date createdTimeStamp;
    private String clientName;
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
    private String taskid;
    private String payrollid;
    private String virtualAccountNumber;
    private String virtualAccountId;
    private List<String> identityCallbackUrls;
    private List<String> employmentCallbackUrls;
    private List<String> incomeCallbackUrls;
    private List<String> allocationCallbackUrls;
    private List<String> insufficientFundCallbackUrls;
    private int accountValidationAttempt;
    private List<String> notificationUrls;
    private List<FlowTypeEnum> flowType;
    private String loginSdkStatus;

}
