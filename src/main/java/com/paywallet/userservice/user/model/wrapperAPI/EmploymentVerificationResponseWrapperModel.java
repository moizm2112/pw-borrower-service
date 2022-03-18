package com.paywallet.userservice.user.model.wrapperAPI;

import lombok.Data;

@Data
public class EmploymentVerificationResponseWrapperModel {
	
	private String employer;
	private String emailId;
	private String mobileNo;
	private String employmentCallbackUrl;
	private String firstName;
	private String lastName;
	private String lenderName;

}
