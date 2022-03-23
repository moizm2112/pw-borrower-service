package com.paywallet.userservice.user.model.wrapperAPI;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class IdentityVerificationRequestWrapperModel {
    @NotNull @NotEmpty @NotBlank
	private String employerId;
	private String identityCallbackUrl;
	private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    @NotNull @NotEmpty @NotBlank
    private String emailId;
    @NotNull @NotEmpty @NotBlank
    private String cellPhone;
    private String dateOfBirth;
    private String last4TIN;
    private String city;
    private String state;
    private String zip;

}
