package com.paywallet.userservice.user.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.SMSAndEmailNotificationException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.LenderConfigInfo;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.SdkCreateCustomerRequest;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationResponseWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationResponseWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationResponseWrapperModel;
import com.paywallet.userservice.user.repository.CustomerRepository;
import com.paywallet.userservice.user.repository.CustomerRequestFieldsRepository;
import com.paywallet.userservice.user.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SdkCustomerService {

	@Autowired
	CustomerService customerService;
	@Autowired
	CustomerRequestFieldsRepository customerRequestFieldsRepository;
	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	CustomerFieldValidator customerFieldValidator;

	@Autowired
	CustomerWrapperAPIService customerWrapperAPIService;
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	CustomerServiceHelper customerServiceHelper;

	@Value("${identifyProviderService.eureka.uri}")
	private String identifyProviderServiceUri;
	
	@Autowired
	private CommonUtil commonUtil;

	public <T> CustomerDetails sdkCreateCustomer(SdkCreateCustomerRequest sdkCustomer, String requestId, T obj,
			FlowTypeEnum flowType) throws CreateCustomerException, GeneralCustomException, ServiceNotAvailableException,
			RequestIdNotFoundException, SMSAndEmailNotificationException {
		log.info("Inside createCustomer of CustomerService class");
		CustomerDetails createCustomer = null;
		CreateCustomerRequest customerRequest = new CreateCustomerRequest();
		// converting sdkcustomerRequest to createCustomerRequest
		CreateCustomerRequest convertedRequestToCreateCustomer = convertSdkCustomerToCustomerRequest(sdkCustomer,
				customerRequest);
		createCustomer = customerService.createCustomer(convertedRequestToCreateCustomer, requestId, sdkCustomer,
				FlowTypeEnum.SDK);
		return createCustomer;

	}

	public CreateCustomerRequest convertSdkCustomerToCustomerRequest(SdkCreateCustomerRequest sdkCreateCustomerRequest,
			CreateCustomerRequest customer) {
		if (sdkCreateCustomerRequest != null) {
			customer.setFirstName(sdkCreateCustomerRequest.getFirstName());
			customer.setLastName(sdkCreateCustomerRequest.getLastName());
			customer.setCellPhone(sdkCreateCustomerRequest.getCellPhone());
			customer.setEmailId(sdkCreateCustomerRequest.getEmailId());
			customer.setCallbackURLs(sdkCreateCustomerRequest.getCallbackURLs());
			customer.setFirstDateOfPayment(sdkCreateCustomerRequest.getFirstDateOfPayment());
			customer.setRepaymentFrequency(sdkCreateCustomerRequest.getRepaymentFrequency());
			customer.setNumberOfInstallments(sdkCreateCustomerRequest.getNumberOfInstallments());
			customer.setInstallmentAmount(sdkCreateCustomerRequest.getInstallmentAmount());
			customer.setZip(sdkCreateCustomerRequest.getZip());
			customer.setState(sdkCreateCustomerRequest.getState());
			customer.setAddressLine1(sdkCreateCustomerRequest.getAddressLine1());
			customer.setAddressLine2(sdkCreateCustomerRequest.getAddressLine2());
			customer.setMiddleName(sdkCreateCustomerRequest.getMiddleName());
			customer.setCity(sdkCreateCustomerRequest.getCity());
			customer.setLast4TIN(sdkCreateCustomerRequest.getLast4TIN());
			customer.setDateOfBirth(sdkCreateCustomerRequest.getDateOfBirth());
		}
		return customer;
	}
	
	
	
}
