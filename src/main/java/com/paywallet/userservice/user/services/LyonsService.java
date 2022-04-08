package com.paywallet.userservice.user.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paywallet.userservice.user.model.LyonsAPIRequestDTO;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class LyonsService {

	@Autowired
	protected ObjectMapper objectMapper;

// 	@Autowired
// 	private RestTemplate restTemplate;

	@Value("${lyons.api.baseURL}")
	private String lyonsBaseURL;

	@Value("${lyons.api.companyId}")
	private int companyId;

	@Value("${lyons.api.userName}")
	private String userName;

	@Value("${lyons.api.password}")
	private String password;

	@Value("${lyons.api.returnDetails}")
	private int returnDetails;

	private static final String ERROR = "Error";
	private static final String ERROR_MESSAGE = "errorMessage";
	private static final String RESULT = "result";
	private static final String TOKEN = "token";
	private static final String REQUEST_STRING = "requestString";
	private static final String COMPANY_ID = "companyId";
	private static final String USER_NAME = "userName";
	private static final String PASSWORD = "password";

	
	public JSONObject checkAccountOwnership(LyonsAPIRequestDTO apiRequest)  {
		JSONObject verificationRequest = createAccountVerificationRequest(apiRequest);
		if (verificationRequest.has(ERROR)) {
			log.info("error in verification request");
			return verificationRequest;
		} else {
			log.info("calling lyons post api to get status");
			return lyonsPostApi("rest/CheckAccountOwnershipAndStatus", verificationRequest.getString(REQUEST_STRING));
		}
	}

	private JSONObject createAccountVerificationRequest(LyonsAPIRequestDTO apiRequest) {
		JSONObject requestDataObject = new JSONObject();

		JSONObject tokenObj = getLyonsToken();

		JSONObject result = tokenObj.has(RESULT) ? tokenObj.getJSONObject(RESULT) : null;

		if (result == null) {
			Object errorMessage = tokenObj.get(ERROR);
			requestDataObject.put(ERROR, errorMessage);
		} else if (!result.has(TOKEN) || result.get(TOKEN) == null) {
			Object errorMessage = result.has(ERROR_MESSAGE) ? result.get(ERROR_MESSAGE) : null;
			if (errorMessage != null) {
				requestDataObject.put(ERROR, errorMessage);
			} else {
				requestDataObject.put(ERROR, "Failed to log on");
			}
		} else {
			apiRequest.setToken(result.get(TOKEN).toString());
			apiRequest.setReturnDetails(returnDetails);
			apiRequest.initRequest();
			try {
				requestDataObject.put(REQUEST_STRING, objectMapper.writeValueAsString(apiRequest));
			} catch (JsonProcessingException e) {
				Sentry.captureException(e);
				requestDataObject.put(ERROR, "Lyons request parsing failed !");
			}
		}
		return requestDataObject;
	}

	private JSONObject getLyonsToken() {
		JSONObject request = new JSONObject();
		request.put(COMPANY_ID, companyId);
		request.put(USER_NAME, userName);
		request.put(PASSWORD, password);
		log.info("get lyons token from login");
		return lyonsPostApi("rest/Logon", request.toString());
	}

	private JSONObject lyonsPostApi(String endpoint, String jsonRequestBody) {
		JSONObject apiResponseData = new JSONObject();
		HttpEntity<String> request = new HttpEntity<>(jsonRequestBody, getRequestHeader());
		try {
			String result = new RestTemplate().postForObject(this.lyonsBaseURL + endpoint, request, String.class);
			apiResponseData.put(RESULT, new JSONObject(result));
		} catch (JSONException err) {
			Sentry.captureException(err);
			apiResponseData.put(ERROR, "Lyons response parsing failed !");
		} catch (HttpClientErrorException ex) {
			Sentry.captureException(ex);
			apiResponseData.put(ERROR, "Error while communicating with Lyons API !");
		}
		return apiResponseData;
	}

	private HttpHeaders getRequestHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

}
