package com.paywallet.userservice.user.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include. NON_NULL)
public class CallbackURL {
	
	private List<String> identityCallbackUrl;
	private List<String> employmentCallbackUrl;
	private List<String> incomeCallbackUrl;
	private List<String> allocationCallbackUrl;
	private List<String> insufficientFundCallbackUrl;

}
