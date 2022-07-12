package com.paywallet.userservice.user.controller;



import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paywallet.userservice.user.constant.AppConstants;
import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.model.SdkCreateCustomerRequest;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.SdkCustomerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(AppConstants.BASE_PATH)
public class SdkCustomerController {
	@Autowired
	SdkCustomerService sdkCustomerService;
	
	@Autowired
	CustomerService customerService;

	 @PostMapping(AppConstants.SDK_CREATE_CUSTOMER)
	    public ResponseEntity<Object> createCustomer(@Valid @RequestBody SdkCreateCustomerRequest sdkCreateCustomerRequest, @RequestHeader(AppConstants.REQUEST_ID) String requestId,
	    		HttpServletRequest request)
	    		throws MethodArgumentNotValidException, CreateCustomerException, RequestIdNotFoundException {
	        log.debug("Inside Create Customer controller " + sdkCreateCustomerRequest);
	        //change name
	        CustomerDetails customerDetails = sdkCustomerService.sdkCreateCustomer(sdkCreateCustomerRequest, requestId, null, FlowTypeEnum.GENERAL);
	        if(customerDetails.isExistingCustomer())
	        	return customerService.prepareResponse(customerDetails, CommonEnum.CUSTOMER_EXIST_SUCCESS_MSG.getMessage(),
	        			HttpStatus.OK.value(), request.getRequestURI());
	        return customerService.prepareResponse(customerDetails, CommonEnum.CUSTOMER_CREATED_SUCCESS_MSG.getMessage(),
	        		HttpStatus.CREATED.value(), request.getRequestURI());		
	    }
	   
}
