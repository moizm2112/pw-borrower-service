package com.paywallet.userservice.user.model.wrapperAPI;

import com.paywallet.userservice.user.model.CallbackURL;

import lombok.Data;

@Data
public class DepositAllocationRequestWrapperModel {

	private String employerId;
	private String emailId;
	private String mobileNo;
	private Integer installmentAmount;
	private Integer totalNoOfRepayment;
	private String achPullRequest;
	private String accountVerificationOverride;
	private String firstDateOfPayment;
	private String externalVirtualAccount;
	private String externalVirtualAccountABANumber;
	private CallbackURL callbackURLs;
	private String repaymentFrequency;
	private String lender;
	private String firstName;
	private String lastName;
}

