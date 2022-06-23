package com.paywallet.userservice.user.entities;

import com.paywallet.userservice.user.util.AESEncryption;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.paywallet.userservice.user.enums.VerificationStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customer")
@Getter
@Setter
@ToString
@Builder
@Slf4j
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

    public void setAccountABANumber(String accountABANumber) {
        this.accountABANumber = encrypt(accountABANumber);
    }

    public void setVirtualAccount(String virtualAccount) {
        this.virtualAccount = encrypt(virtualAccount);
    }

    public void setSalaryAccountNumber(String salaryAccountNumber) {
        this.salaryAccountNumber = encrypt(salaryAccountNumber);
    }

    public void setExternalAccount(String externalAccount) {
        this.externalAccount = encrypt(externalAccount);
    }

    public void setExternalAccountABA(String externalAccountABA) {
        this.externalAccountABA = encrypt(externalAccountABA);
    }

    public String getAccountABANumber() {
        return decrypt(this.accountABANumber);
    }

    public String getVirtualAccount() {
        return decrypt(this.virtualAccount);
    }

    public String getSalaryAccountNumber() {
        return decrypt(this.salaryAccountNumber);
    }

    public String getExternalAccount() {
        return decrypt(this.externalAccount);
    }

    public String getExternalAccountABA() {
        return decrypt(this.externalAccountABA);
    }

    public String encrypt(String plainText) {
        String encText = null;
        try {
            encText = AESEncryption.encrypt(plainText);
        } catch (Exception e) {
            log.error("Error while encrypting :{}",e.getMessage(),e);
            throw new RuntimeException("Error while encryption :"+e.getMessage());
        }
        return encText;
    }
    public String decrypt(String cipherText) {
        String decText = null;
        try {
            decText = AESEncryption.decrypt(cipherText);
        } catch (Exception e) {
            log.error("Error while decrypting :{}",e.getMessage(),e);
            throw new RuntimeException("Error while decryption :"+e.getMessage());
        }
        return decText;
    }
}
