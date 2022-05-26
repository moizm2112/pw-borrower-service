package com.paywallet.userservice.user.dto;

import com.paywallet.userservice.user.util.CommonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PayrollProviderDetailsDTO {
    private String requestId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String addressLine1;
    private String addressLine2;
    private String emailId;
    private String cellPhone;
    private String dateOfBirth;
    private String last4TIN;
    private String city;
    private String state;
    private String zip;

    @Override
    public String toString() {
        CommonUtil commonUtil = new CommonUtil();
        return new StringBuilder("{")
                .append("requestId: ")
                .append(requestId)
                .append(",")
                .append("firstName: ")
                .append(firstName)
                .append(",")
                .append("lastName: ")
                .append(lastName)
                .append(",")
                .append("middleName: ")
                .append(middleName)
                .append(",")
                .append("addressLine1: ")
                .append(addressLine1)
                .append(",")
                .append("addressLine2: ")
                .append(addressLine2)
                .append(",")
                .append("emailId: ")
                .append(commonUtil.hashString(emailId))
                .append(",")
                .append("cellPhone: ")
                .append(commonUtil.hashString(cellPhone))
                .append(",")
                .append("dateOfBirth: ")
                .append(commonUtil.hashString(dateOfBirth))
                .append(",")
                .append("last4TIN: ")
                .append(commonUtil.hashString(last4TIN))
                .append(",")
                .append("city: ")
                .append(city)
                .append(",")
                .append("state: ")
                .append(state)
                .append(",")
                .append("zip: ")
                .append(zip)
                .append("}")
                .toString();
    }
}
