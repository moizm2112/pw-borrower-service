package com.paywallet.userservice.user.model;

import com.paywallet.userservice.user.util.CustomerServiceUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.paywallet.userservice.user.constant.AppConstants.MOBILENO_NULL_VALIDATION_MESSAGE;

@Getter
@Setter
public class ValidateAccountRequest {
    @Pattern(regexp = "^[1][1-9]\\d{9}$"+"|^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$" + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$")
    @NotBlank(message =MOBILENO_NULL_VALIDATION_MESSAGE)
    private String mobileNo;
    private String salaryAccountNumber;
    private String accountABANumber;

    @Override
    public String toString(){
        return new StringBuilder("{")
                .append("mobileNo:").append(CustomerServiceUtil.mask(mobileNo)).append(",")
                .append("salaryAccountNumber:").append(CustomerServiceUtil.hashString(salaryAccountNumber)).append(",")
                .append("accountABANumber:").append(CustomerServiceUtil.hashString(accountABANumber)).append("}")
                .toString();
    }
}
