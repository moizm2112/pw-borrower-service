package com.paywallet.userservice.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class UpdateCustomerDetailsResponseDTO {
	@JsonInclude(JsonInclude.Include. NON_NULL)
	private String mobileNo;
	@JsonInclude(JsonInclude.Include. NON_NULL)
	private String emailId;
	@JsonInclude(JsonInclude.Include. NON_NULL)
	private String clientId;
	@JsonInclude(JsonInclude.Include. NON_NULL)
	private String requestId;
	@JsonInclude(JsonInclude.Include. NON_NULL)
	private String customerId;

}
