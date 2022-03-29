package com.paywallet.userservice.user.model.wrapperAPI;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.paywallet.userservice.user.model.CallbackURL;

import lombok.Data;

@Data
public class EmploymentVerificationRequestWrapperModel {
	@NotNull @NotEmpty @NotBlank
	private String employerId;
	@NotNull @NotEmpty @NotBlank
	private String emailId;
	@NotNull @NotEmpty @NotBlank
	private String cellPhone;
//	private String employmentCallbackUrl;
	private CallbackURL callbackURLs;
	private String firstName;
	private String lastName;
	private String lender;

}
