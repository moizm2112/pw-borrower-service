package com.paywallet.userservice.user.model;

import lombok.Data;

@Data
public class UpdateCustomerCredentialsResponse {
	
	private String mobileNo;
	private String mobileNoVerified;
	private String emailId;
	private String emailIdVerified;

}
