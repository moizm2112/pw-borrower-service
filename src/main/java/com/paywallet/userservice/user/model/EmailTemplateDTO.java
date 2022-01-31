package com.paywallet.userservice.user.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplateDTO {
	
	private String templateId;
	private String emailSubject;
	private Map<String, String> emailBody;

}
