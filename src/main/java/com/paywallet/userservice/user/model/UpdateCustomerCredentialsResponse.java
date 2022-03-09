package com.paywallet.userservice.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class UpdateCustomerCredentialsResponse {
	
	private String mobileNo;
	private String mobileNoVerified;
	@JsonInclude(JsonInclude.Include. NON_NULL)
	private String emailId;
	private String emailIdVerified;
	private String requestId;

}
