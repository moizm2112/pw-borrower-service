package com.paywallet.userservice.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalInfoDTO {

    private String allocationAmount;
    private String installments;
    private String lender;
    private String firstName;
    private String lastName;
    private String middleName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String telePhone;
    private String last4Tin;
    private String salaryAccountNumber;
    private String abaNumber;
    private String customerId;
    private String cellPhone;
    private String argyleUserId;
    private String taskId;
    private String preferredProvider;
}
