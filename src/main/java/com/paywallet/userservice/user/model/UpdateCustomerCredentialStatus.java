package com.paywallet.userservice.user.model;

import com.paywallet.userservice.user.enums.VerificationStatusEnum;

import lombok.Data;

@Data
public class UpdateCustomerCredentialStatus {
	
	private String customerId;
	private VerificationStatusEnum cellPhoneVerificationStatus;
	private VerificationStatusEnum emailIdVerificationStatus;

}
