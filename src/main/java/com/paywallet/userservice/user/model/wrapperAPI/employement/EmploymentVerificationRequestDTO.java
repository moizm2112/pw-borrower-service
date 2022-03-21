package com.paywallet.userservice.user.model.wrapperAPI.employement;

import org.springframework.stereotype.Component;

import com.paywallet.userservice.user.model.CallbackURL;

import lombok.Data;

@Data
@Component
public class EmploymentVerificationRequestDTO {

    private String employerId;
    private String emailId;
    private String mobileNo;
    private String firstName;
    private String lastName;
    private String middleName;
    private CallbackURL callbackUrls;

}
