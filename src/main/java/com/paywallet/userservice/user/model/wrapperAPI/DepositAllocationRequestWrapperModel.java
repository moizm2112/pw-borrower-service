package com.paywallet.userservice.user.model.wrapperAPI;

import com.paywallet.userservice.user.model.CallbackURL;

import lombok.Data;

@Data
public class DepositAllocationRequestWrapperModel {

	private String employerId;
	private String emailId;
	private String cellPhone;
	private Integer loanAmount;
	private Integer installmentAmount;
	private Integer numberOfInstallments;
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

