package com.paywallet.userservice.user.entities;

import com.paywallet.userservice.user.util.CommonUtil;
import lombok.Data;

@Data
public class PayrollProfile {
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
    private String updatedAt;

    @Override
    public String toString() {
        CommonUtil commonUtil = new CommonUtil();
        return new StringBuilder("{")
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
                .append(",")
                .append("updatedAt: ")
                .append(updatedAt)
                .append("}")
                .toString();
    }
}
