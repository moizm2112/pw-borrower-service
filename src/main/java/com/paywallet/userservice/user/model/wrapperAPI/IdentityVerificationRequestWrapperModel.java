package com.paywallet.userservice.user.model.wrapperAPI;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class IdentityVerificationRequestWrapperModel {
	@NotNull
	private String employerId;
	private String identityCallbackUrl;
	private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    @NotNull
    private String emailId;
    @NotNull
    private String cellPhone;
    private String dateOfBirth;
    private String last4TIN;
    private String city;
    private String state;
    private String zip;

}
