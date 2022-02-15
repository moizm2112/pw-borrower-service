package com.paywallet.userservice.user.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailRequestDTO {

	private String to;
	private String subject;
	private String  templateId;
	private Map<String, String> templateBody;
	private String body;
	private String requestor;
	private String requestId;
	private String requestType;
	
	
}
