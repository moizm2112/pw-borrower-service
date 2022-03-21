package com.paywallet.userservice.user.model.wrapperAPI;

import lombok.Data;

@Data
public class IncomeVerificationRequestWrapperModel {
	
	private String employerId;
	private String emailId;
	private String cellPhone;
	private String incomeCallbackUrl;
	private String firstName;
	private String lastName;
	private String lender;
	private String numberOfMonthsRequested;

}
