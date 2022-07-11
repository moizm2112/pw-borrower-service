package com.paywallet.userservice.user.model;

import java.util.List;

import com.paywallet.userservice.user.enums.SdkContextEnum;
import com.paywallet.userservice.user.enums.ServicesSelectedEnum;
import lombok.Data;

@Data
public class SdkCreateCustomerRequest {

	private String firstName;

	private String lastName;

	private String middleName;

	private String addressLine1;

	private String addressLine2;

	private String city;

	private String state;

	private String zip;

	private String last4TIN;

	private String dateOfBirth;

	private String emailId;

	private String cellPhone;

	private CallbackURL callbackURLs;

	private String firstDateOfPayment;

	private String repaymentFrequency;

	private Integer numberOfInstallments;

	private Integer installmentAmount;

	private Boolean checkOutExperience;

	private List<SdkContextEnum> sdkContext;

	private List<ServicesSelectedEnum> servicesSelected;

	private boolean existingCustomer;

	private String requestId;
	private String employerId;
	private String lender;
	private String numberOfMonthsRequested;

	private Integer loanAmount;

	private String achPullRequest;
	private String accountVerificationOverride;

	private String externalVirtualAccount;
	private String externalVirtualAccountABANumber;
}
