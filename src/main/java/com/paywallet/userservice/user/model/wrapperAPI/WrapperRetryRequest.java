package com.paywallet.userservice.user.model.wrapperAPI;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.paywallet.userservice.user.model.CallbackURL;

import lombok.Data;

@Data
public class WrapperRetryRequest {
	@NotNull @NotEmpty @NotBlank
	private String employerId;
	@NotNull @NotEmpty @NotBlank
	private String emailId;
	@NotNull @NotEmpty @NotBlank
	private String cellPhone;
}
