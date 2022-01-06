package com.paywallet.userservice.user.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class CustomerDetails {
    @Id
    private String customerId;

    private PersonalProfile personalProfile;
    @JsonInclude(value = Include.NON_NULL)
    private SalaryProfile salaryProfile;

    private String salaryAccountNumber;
    private String abaOfSalaryAccount;
    private String financedAmount;
    private Integer updateCounter=0;
    private String status="";
    private String lender;
    private String virtualAccount;
    @JsonIgnore
    private String requestId;
    @JsonIgnore
    private boolean existingCustomer;

}
