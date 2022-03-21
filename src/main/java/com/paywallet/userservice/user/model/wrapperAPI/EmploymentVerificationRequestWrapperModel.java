package com.paywallet.userservice.user.model.wrapperAPI;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class EmploymentVerificationRequestWrapperModel {
	@NotNull
	private String employerId;
	@NotNull
	private String emailId;
	@NotNull
	private String cellPhone;
	private String employmentCallbackUrl;
	private String firstName;
	private String lastName;
	private String lender;

}
