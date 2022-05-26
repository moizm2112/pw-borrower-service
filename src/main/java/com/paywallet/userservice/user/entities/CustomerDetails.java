package com.paywallet.userservice.user.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.paywallet.userservice.user.enums.VerificationStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customer")
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include. NON_NULL)
public class CustomerDetails {
    @Id
    private String customerId;

    private PersonalProfile personalProfile;
    @JsonInclude(value = Include.NON_NULL)
    private SalaryProfile salaryProfile;

    private String salaryAccountNumber;
    private String accountABANumber;
    private String financedAmount;
    private Integer updateCounter=0;
    private String status="";
    private String lender;
    private String virtualAccount;
    private String employer;
    @JsonIgnore
    private String requestId;
    @JsonIgnore
    private boolean existingCustomer;
    @JsonIgnore
    private String firstDateOfPayment;
    @JsonIgnore
    private String repaymentFrequency;
    @JsonIgnore
    private int numberOfInstallments;
    @JsonIgnore
    private int installmentAmount;
    @JsonIgnore
    private int loanAmount;
    @JsonIgnore
    private boolean emailNotificationSuccess;
    @JsonIgnore
    private boolean smsNotificationSuccess;
    private String virtualAccountId;
    private String virtualClientId;
    private Boolean checkOutExperience;
    private VerificationStatusEnum cellPhoneVerificationStatus;
    
   	private VerificationStatusEnum emailIdVerificationStatus;
   	private String externalAccount;
   	private String externalAccountABA;

       private PayrollProfile payrollProvidedDetails;


}
