package com.paywallet.userservice.user.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(value = Include.NON_NULL)
public class SalaryProfile {
    private String grossSalary;
    private String netSalary;
    private String salaryAccount;
    private String aba;
    private String salaryFrequency;
    private String provider;
    private String token;
}
