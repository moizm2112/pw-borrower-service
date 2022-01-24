package com.paywallet.userservice.user.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include. NON_NULL)
public class CallbackURL {
	
	private List<String> identityCallbackUrls;
	private List<String> employmentCallbackUrls;
	private List<String> incomeCallbackUrls;
	private List<String> allocationCallbackUrls;
	private List<String> insufficientFundCallbackUrls;

}
