package com.paywallet.userservice.user.model;

import static com.paywallet.userservice.user.constant.AppConstants.MOBILENO_FORMAT_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.MOBILENO_LENGTH_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.MOBILENO_NULL_VALIDATION_MESSAGE;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateCustomerCredentialsModel {
	
	@Pattern(regexp = "^(\\+\\d{1,2})?\\(?\\d{3}\\)?\\d{3}?\\d{4}$", message = MOBILENO_FORMAT_VALIDATION_MESSAGE)
	@Size(min = 10, max = 13, message = MOBILENO_LENGTH_VALIDATION_MESSAGE)
	@NotBlank  (message = MOBILENO_NULL_VALIDATION_MESSAGE)
	private String mobileNo;
	
	private String newMobileNo;
	
	private String emailId;
	
	private String newEmailId;

}
