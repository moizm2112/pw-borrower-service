package com.paywallet.userservice.user.model.wrapperAPI;

import lombok.Data;

@Data
public class IdentityVerificationRequestWrapperModel {
	
	private String employerId;
	private String identityCallbackUrl;
	private String firstName;
    private String lastName;
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
