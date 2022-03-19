package com.paywallet.userservice.user.model.wrapperAPI;

import lombok.Data;

@Data
public class IncomeVerificationResponseWrapperModel {
	
	private String employer;
	private String emailId;
	private String mobileNo;
	private String incomeCallbackUrl;
	private String firstName;
	private String lastName;
	private String numberOfMonthsRequested;

}
