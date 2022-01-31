package com.paywallet.userservice.user.model;

import static com.paywallet.userservice.user.constant.AppConstants.MOBILENO_NULL_VALIDATION_MESSAGE;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.paywallet.userservice.user.entities.SalaryProfile;

import com.paywallet.userservice.user.util.CustomerServiceUtil;
import lombok.Data;

@Data
public class UpdateCustomerRequestDTO  {
    @Pattern(regexp = "^[1][1-9]\\d{9}$"+"|^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$" + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$")
    @NotBlank(message =MOBILENO_NULL_VALIDATION_MESSAGE)
    private String mobileNo;
    private SalaryProfile salaryProfile;
    @Override
    public String toString(){
        return new StringBuilder("{")
                .append("mobileNo").append(CustomerServiceUtil.mask(mobileNo)).append(",")
                .append("salaryProfile").append(salaryProfile).append("}")
                .toString();
    }

}
