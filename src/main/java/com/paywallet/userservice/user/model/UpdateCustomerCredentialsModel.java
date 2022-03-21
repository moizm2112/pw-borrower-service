package com.paywallet.userservice.user.model;

import static com.paywallet.userservice.user.constant.AppConstants.CELLPHONE_FORMAT_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.CELLPHONE_LENGTH_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.CELLPHONE_NULL_VALIDATION_MESSAGE;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateCustomerCredentialsModel {
	
	@Pattern(regexp = "^(\\+\\d{1,2})?\\(?\\d{3}\\)?\\d{3}?\\d{4}$", message = CELLPHONE_FORMAT_VALIDATION_MESSAGE)
	@Size(min = 10, max = 13, message = CELLPHONE_LENGTH_VALIDATION_MESSAGE)
	@NotBlank  (message = CELLPHONE_NULL_VALIDATION_MESSAGE)
	private String cellPhone;
	
	private String newCellPhone;
	
	private String emailId;
	
	private String newEmailId;

}
