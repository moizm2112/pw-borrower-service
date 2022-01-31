package com.paywallet.userservice.user.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.paywallet.userservice.user.util.CustomerServiceUtil;
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

    @Override
    public String toString(){
        return new StringBuilder("{")
                .append("grossSalary").append(grossSalary).append(",")
                .append("netSalary").append(netSalary).append(",")
                .append("salaryAccount").append(CustomerServiceUtil.hashString(salaryAccount)).append(",")
                .append("aba").append(CustomerServiceUtil.hashString(aba)).append(",")
                .append("salaryFrequency").append(salaryFrequency).append(",")
                .append("provider").append(provider).append(",")
                .append("token").append(token).append("}")
                .toString();
    }
}
