package com.paywallet.userservice.user.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paywallet.userservice.user.enums.ServicesSelectedEnum;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationRequestWrapperModel;

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
	@JsonIgnore
	private SdkContext sdkContext;

	private List<ServicesSelectedEnum> servicesSelected;
	@JsonIgnore
	private boolean existingCustomer;
	@JsonIgnore
	private String requestId;
	private EmploymentVerificationRequestWrapperModel employmentVerification;
	private IncomeVerificationRequestWrapperModel incomeVerification;
	private IdentityVerificationRequestWrapperModel identityVerification;
	private DepositAllocationRequestWrapperModel depositAllocation;

}
