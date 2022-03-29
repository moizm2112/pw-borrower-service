package com.paywallet.userservice.user.model.wrapperAPI;

import com.paywallet.userservice.user.model.CallbackURL;

import lombok.Data;

@Data
public class IdentityVerificationResponseWrapperModel {
	
	private String employer;
	private String emailId;
	private String cellPhone;
	private CallbackURL callbackURLs;
	private String firstName;
	private String lastName;
	private String last4TIN;

}
