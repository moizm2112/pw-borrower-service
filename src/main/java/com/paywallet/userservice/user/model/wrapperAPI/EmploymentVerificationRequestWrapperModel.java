package com.paywallet.userservice.user.model.wrapperAPI;

import lombok.Data;

@Data
public class EmploymentVerificationRequestWrapperModel {
	
	private String employerId;
	private String emailId;
	private String cellPhone;
	private String employmentCallbackUrl;
	private String firstName;
	private String lastName;
	private String lender;

}
