package com.paywallet.userservice.user.controller;

import static com.paywallet.userservice.user.constant.AppConstants.BASE_PATH;
import static com.paywallet.userservice.user.constant.AppConstants.CUSTOMER_CONTACT_VERFICATION_UPDATE;
import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.model.UpdateCustomerCredentialStatus;
import com.paywallet.userservice.user.services.CustomerCredentialVerificationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(BASE_PATH)
public class CustomerCredentialVerificationController {

	@Autowired
	CustomerCredentialVerificationService customerCredentialVerificationService;

	@PatchMapping(value = CUSTOMER_CONTACT_VERFICATION_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateCustomerCredentialVerificationStatus(
			@RequestHeader(required = true, value = REQUEST_ID) String requestId,
			@RequestBody UpdateCustomerCredentialStatus customerCredentialStatus, HttpServletRequest request) {

		log.info("request for update of credential verification status:: " + customerCredentialStatus);

		CustomerDetails customerResponse = customerCredentialVerificationService
				.updateCustCredVerificationStatus(requestId, customerCredentialStatus);

		return customerCredentialVerificationService.prepareUpdateResponse(customerResponse,
				CommonEnum.SUCCESS_STATUS_MSG.getMessage(), HttpStatus.OK.value(), request.getRequestURI());

	}
}
