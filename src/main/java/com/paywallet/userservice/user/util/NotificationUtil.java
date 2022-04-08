package com.paywallet.userservice.user.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.model.EmailRequestDTO;
import com.paywallet.userservice.user.model.EmailTemplateDTO;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.SmsRequestDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationUtil {
	@Autowired
	RestTemplate restTemplate;

	private static final String REQUESTOR_ID = "create_customer";
	private static final String SMS_URI = "notifications/sms";
	private static final String EMAIL_URI = "notifications/email";
	public static final String STATUS_OK = "200 OK";
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	private static final String EMAIL = "email";
	private static final String LINK = "link";
	private static final String EMPLOYER = "employer";
	private static final String LENDER = "lender";
	private static final String REQUEST_TYPE = "Login link";
	
	@Value("${notification.uri}")
	private String basePath;
	@Value("${smsLinkTemplate}")
	private String smsLinkTemplate;
	@Value("${emailTemplateId}")
	private String emailTemplateId;
	

	public String callNotificationService(RequestIdDetails requestIdDetails, CustomerDetails customerDetail, String linkForCustomer) {
		log.info("inside callNotificationService");
		String smsResponse = null;
		String emailResponse = null;
		String smsEmailResponseValidator = null;
		try {
			SmsRequestDTO smsRequest = new SmsRequestDTO();
			EmailRequestDTO emailRequest = new EmailRequestDTO();
			String phoneNumber = customerDetail.getPersonalProfile().getCellPhone();
			String emailAddress = customerDetail.getPersonalProfile().getEmailId();
			smsRequest = creatSmsRequest(phoneNumber, linkForCustomer,requestIdDetails);
			emailRequest = createEmailRequestForCustomer(emailAddress, linkForCustomer, requestIdDetails);
			final String smsUrl = basePath + SMS_URI;
			final String emailUrl = basePath + EMAIL_URI;
			emailResponse = callEmailNotificationApi(emailRequest, emailUrl);
			smsResponse = callSmsNotificationApi(smsRequest, smsUrl);
			
			smsEmailResponseValidator = validateSmsEmailResponse(smsResponse, emailResponse, customerDetail);
			if (FAIL.equalsIgnoreCase(smsEmailResponseValidator)) {
				throw new GeneralCustomException("SMS and Email notification to customer failed",
						HttpStatus.INTERNAL_SERVER_ERROR.toString());
			}
		}
		catch(Exception e) {
			Sentry.captureException(e);
			log.error("callNotificationService Exception : " + e.getMessage());
			if(emailResponse != null || smsResponse != null) {
				smsEmailResponseValidator = validateSmsEmailResponse(smsResponse, emailResponse, customerDetail);
				if (FAIL.equalsIgnoreCase(smsEmailResponseValidator)) {
					throw new GeneralCustomException("SMS and Email notification to customer failed",
							HttpStatus.INTERNAL_SERVER_ERROR.toString());
				}
				else {
					return smsEmailResponseValidator;
				}
			}
		}
		
		return smsEmailResponseValidator;

	}

	private String validateSmsEmailResponse(String smsResponse, String emailResponse, CustomerDetails customerDetail) {
		
		if(emailResponse != null && emailResponse.equalsIgnoreCase(STATUS_OK))
			customerDetail.setEmailNotificationSuccess(true);
		else
			customerDetail.setEmailNotificationSuccess(false);
		if(smsResponse != null && smsResponse.equalsIgnoreCase(STATUS_OK))
			customerDetail.setSmsNotificationSuccess(true);
		else
			customerDetail.setSmsNotificationSuccess(false);
		
		if (STATUS_OK.equalsIgnoreCase(smsResponse) || STATUS_OK.equalsIgnoreCase(emailResponse))
			return SUCCESS;
		
		return FAIL;
	}

	private String callEmailNotificationApi(EmailRequestDTO emailRequest, String emailUrl) {
		String emailResponse = null;
		URI uriEmail = null;
		try {
			uriEmail = new URI(emailUrl);
		} catch (URISyntaxException e) {
			Sentry.captureException(e);
			log.error("error encountered during email notify call " + e.getMessage());
		}
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(uriEmail, emailRequest, String.class);
			emailResponse = response.getStatusCode().toString();
			log.info("return response from email notification " + response.getBody());
		} catch (HttpClientErrorException e) {
			Sentry.captureException(e);
			log.error("error encountered during sent email call ");
		}

		return emailResponse;
	}

	private String callSmsNotificationApi(SmsRequestDTO smsRequest, String smsUrl) {
		URI uriSms = null;
		RestTemplate restTemplate = new RestTemplate();
		String smsResponse = null;
		try {
			uriSms = new URI(smsUrl);
		} catch (URISyntaxException e) {
			Sentry.captureException(e);
			log.error("error encountered during sms notify call " + e.getMessage());
		}
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(uriSms, smsRequest, String.class);
			log.info("return response from sms notification " + response.getBody());
			smsResponse = response.getStatusCode().toString();
		} catch (HttpClientErrorException e) {
			Sentry.captureException(e);
			log.error("error encountered during sent sms call ");
		}
		return smsResponse;
	}

	private EmailRequestDTO createEmailRequestForCustomer(String emailAddress, String linkForCustomer, RequestIdDetails requestIdDetails) {
		EmailTemplateDTO emailTempDto = createEmailRequest(emailAddress, linkForCustomer,requestIdDetails);
		EmailRequestDTO emailRequest = EmailRequestDTO.builder().templateId(emailTempDto.getTemplateId())
				.templateBody(emailTempDto.getEmailBody()).subject(emailTempDto.getEmailSubject()).body("")
				.to(emailAddress).requestor(requestIdDetails.getClientName()).requestId(requestIdDetails.getRequestId())
				.requestType(REQUEST_TYPE)
				.build();
		return emailRequest;
	}

	private EmailTemplateDTO createEmailRequest(String emailAddress, String linkForCustomer, RequestIdDetails requestIdDetails) {
		log.info("Inside create Email Request ");
		EmailTemplateDTO emailTemplate = new EmailTemplateDTO();
		HashMap<String, String> eMailBodyMap = new HashMap<>();

		emailTemplate.setEmailSubject("Link to proceed for SDK login");
		emailTemplate.setTemplateId(emailTemplateId);
		eMailBodyMap.put(EMAIL, emailAddress);
		eMailBodyMap.put(LINK, linkForCustomer);
		eMailBodyMap.put(EMPLOYER, requestIdDetails.getEmployer());
		eMailBodyMap.put(LENDER, requestIdDetails.getClientName());

		emailTemplate.setEmailBody(eMailBodyMap);
		log.info("emailTemplate for customer " + emailTemplate);
		return emailTemplate;
	}
	
	private SmsRequestDTO creatSmsRequest(String phoneNumber, String linkForCustomer, RequestIdDetails requestIdDetails) {
		log.info("Inside create SMS Request ");
		String messageBody = null;
		 messageBody = String.format(smsLinkTemplate, linkForCustomer,
				 requestIdDetails.getEmployer(),requestIdDetails.getClientName());
		SmsRequestDTO smsRequest = SmsRequestDTO.builder().to(phoneNumber).requestor(requestIdDetails.getClientName()).requestId(requestIdDetails.getRequestId())
				.requestType(REQUEST_TYPE)
				.body(messageBody).build();
		log.info("create SMS Request : " + smsRequest);
		return smsRequest;
	}

}
