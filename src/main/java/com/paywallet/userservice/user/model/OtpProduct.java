package com.paywallet.userservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpProduct {
	
	private String requestId;

	private Integer expiryTimeInMinutes;

}
