package com.paywallet.userservice.user.model.wrapperAPI;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class IncomeVerificationRequestWrapperModel {
	@NotNull
	private String employerId;
	@NotNull
	private String emailId;
	@NotNull
	private String cellPhone;
	private String incomeCallbackUrl;
	private String firstName;
	private String lastName;
	private String lender;
	private String numberOfMonthsRequested;

}
