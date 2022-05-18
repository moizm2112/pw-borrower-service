package com.paywallet.userservice.user.entities;

import lombok.Data;

import java.util.Date;

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
}
