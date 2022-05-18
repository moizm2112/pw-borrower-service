package com.paywallet.userservice.user.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
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
}
