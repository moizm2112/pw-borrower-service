package com.paywallet.userservice.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsRequestDTO {

		private String to;
		private String body;
		private String requestor;
		private String requestId;
	
	
}
