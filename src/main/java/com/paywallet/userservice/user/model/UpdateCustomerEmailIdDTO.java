package com.paywallet.userservice.user.model;

import static com.paywallet.userservice.user.constant.AppConstants.EMAIL_FORMAT_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.EMAIL_NULL_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.CELLPHONE_FORMAT_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.CELLPHONE_LENGTH_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.CELLPHONE_NULL_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.EMPLOYERNAME_NULL_VALIDATION_MESSAGE;
import static com.paywallet.userservice.user.constant.AppConstants.UPDATING_EMAIL_NULL_VALIDATION_MESSAGE;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateCustomerEmailIdDTO {
	
	@Email(message = EMAIL_FORMAT_VALIDATION_MESSAGE)
	@Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9+_-]+(\\.[A-Za-z0-9+_-]+)*@" 
	        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",message = EMAIL_FORMAT_VALIDATION_MESSAGE)
	@NotBlank (message = EMAIL_NULL_VALIDATION_MESSAGE)
	private String emailId;
	
	@Email(message = EMAIL_FORMAT_VALIDATION_MESSAGE)
	@Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9+_-]+(\\.[A-Za-z0-9+_-]+)*@" 
	        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",message = EMAIL_FORMAT_VALIDATION_MESSAGE)
	@NotBlank (message = UPDATING_EMAIL_NULL_VALIDATION_MESSAGE)
	private String newEmailId;
	
	@Pattern(regexp = "^(\\+\\d{1,2})?\\(?\\d{3}\\)?\\d{3}?\\d{4}$", message = CELLPHONE_FORMAT_VALIDATION_MESSAGE)
	@Size(min = 10, max = 13, message = CELLPHONE_LENGTH_VALIDATION_MESSAGE)
	@NotBlank  (message = CELLPHONE_NULL_VALIDATION_MESSAGE)
	private String cellPhone;
	
	@NotBlank(message = EMPLOYERNAME_NULL_VALIDATION_MESSAGE)
	private String employerName;

}
