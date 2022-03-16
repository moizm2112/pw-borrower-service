package com.paywallet.userservice.user.controller;

import static com.paywallet.userservice.user.constant.AppConstants.BASE_PATH;
import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;
import static com.paywallet.userservice.user.constant.AppConstants.UPDATE_CUSTOMER_CREDENTIALS;
import static com.paywallet.userservice.user.constant.AppConstants.INITIATE_DEPOSIT_ALLOCATION;
import static com.paywallet.userservice.user.constant.AppConstants.INITIATE_EMPLOYMENT_VERIFICATION;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.model.UpdateCustomerCredentialsModel;
import com.paywallet.userservice.user.model.UpdateCustomerCredentialsResponse;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationResponseWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationResponseWrapperModel;
import com.paywallet.userservice.user.services.CustomerWrapperAPIService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(BASE_PATH)
public class CustomerWrapperApiController {
	
	@Autowired
	CustomerWrapperAPIService customerWrapperAPIService;
	
	@PutMapping(UPDATE_CUSTOMER_CREDENTIALS)
	public ResponseEntity<Object> updateCustomerCredentials(@Valid @RequestBody UpdateCustomerCredentialsModel updateCustomerCredentialsModel,
			@RequestHeader(REQUEST_ID) String requestId, HttpServletRequest request) throws MethodArgumentNotValidException, RequestIdNotFoundException{
		
		log.info("Inside updateCustomerCredentials " + updateCustomerCredentialsModel);
		UpdateCustomerCredentialsResponse updateCustomerCredentialsResponse = customerWrapperAPIService.updateCustomerCredentials(updateCustomerCredentialsModel, requestId);
		Optional.ofNullable(updateCustomerCredentialsResponse).orElseThrow(() -> new GeneralCustomException("ERROR", "Exception occured while updating the customer credentials"));
		return customerWrapperAPIService.prepareUpdateResponse(updateCustomerCredentialsResponse, 
				CommonEnum.UPDATE_CUSTOMER_CREDENTIALS_SUCCESS_STATUS_MSG.getMessage(), HttpStatus.OK.value(), request.getRequestURI());
	}
	
	@PostMapping(INITIATE_DEPOSIT_ALLOCATION)
	public ResponseEntity<Object> inititateDepositAllocation(@Valid @RequestBody DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel,
			@RequestHeader(REQUEST_ID) String requestId, HttpServletRequest request) throws MethodArgumentNotValidException, RequestIdNotFoundException {
		
		log.info("Inside inititateDepositAllocation " + depositAllocationRequestWrapperModel);
		DepositAllocationResponseWrapperModel depositAllocationResponse = customerWrapperAPIService.initiateDepositAllocation(depositAllocationRequestWrapperModel,
				requestId);
		Optional.ofNullable(depositAllocationResponse).orElseThrow(() -> new GeneralCustomException("ERROR", "Exception occured while deposit allocation"));
		return customerWrapperAPIService.prepareUpdateResponse(depositAllocationResponse, 
				CommonEnum.DEPOSIT_ALLOCATION_SUCCESS_STATUS_MSG.getMessage(), HttpStatus.OK.value(), request.getRequestURI());
	}
	
	/*@PostMapping(INITIATE_EMPLOYMENT_VERIFICATION)
	public ResponseEntity<Object> inititateEmploymentVerification(@Valid @RequestBody EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel,
			@RequestHeader(REQUEST_ID) String requestId, HttpServletRequest request) throws MethodArgumentNotValidException, RequestIdNotFoundException {
		
		log.info("Inside inititateEmploymentVerification " + employmentVerificationRequestWrapperModel);
		EmploymentVerificationResponseWrapperModel employmentVerificationResponse = customerWrapperAPIService.inititateEmploymentVerification(employmentVerificationRequestWrapperModel,
				requestId);
		Optional.ofNullable(employmentVerificationResponse).orElseThrow(() -> new GeneralCustomException("ERROR", "Exception occured while employment verification"));
		return customerWrapperAPIService.prepareUpdateResponse(employmentVerificationResponse, 
				CommonEnum.EMPLOYMENT_VERIFICATION_SUCCESS_STATUS_MSG.getMessage(), HttpStatus.OK.value(), request.getRequestURI());
	}*/
}
