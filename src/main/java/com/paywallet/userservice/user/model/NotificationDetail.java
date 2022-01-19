package com.paywallet.userservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDetail {
	
	private String installmentAmount;
	private String payCycle;
	private String employer;
	private String lender;
}
