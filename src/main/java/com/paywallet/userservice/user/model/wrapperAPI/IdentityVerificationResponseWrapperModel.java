package com.paywallet.userservice.user.model.wrapperAPI;

import lombok.Data;

@Data
public class IdentityVerificationResponseWrapperModel {
	
	private String employer;
	private String emailId;
	private String cellPhone;
	private String identityCallbackUrl;
	private String firstName;
	private String lastName;
	private String last4TIN;

}
