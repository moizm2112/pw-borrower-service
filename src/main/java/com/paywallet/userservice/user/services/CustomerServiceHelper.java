package com.paywallet.userservice.user.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paywallet.userservice.user.constant.AppConstants;
import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.entities.PersonalProfile;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.*;
import com.paywallet.userservice.user.model.*;
import com.paywallet.userservice.user.repository.CustomerRepository;
import com.paywallet.userservice.user.util.NotificationUtil;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;

@Component
@Slf4j
@RefreshScope
public class CustomerServiceHelper {

	private static final String ERROR = "Error";
	private static final String YES = "yes";

	/**
	 * This attribute holds the URI path of the Account service provider
	 * Microservice to create fineract account
	 */
	@Value("${createVirtualAccount.eureka.uri}")
	private String createVirtualAccountUri;

	@Value("${updateVirtualAccount.uri}")
	private String updateVirtualAccountUri;

	@Value("${employer.uri}")
	private String searchEmployerUri;

	@Autowired
	RestTemplate restTemplate;

	@Value("${fineract.clienttype}")
	private String fineractClientType;

	@Value("${link.login.domainname}")
	private String domainNameForLink;

	@Value("${create.link.uri}")
	private String createLinkUri;
	
	@Value("${employer.search.uri}")
	private String employerSearchServicePath;

	@Autowired
	NotificationUtil notificationUtil;

	private static final String LINK_REQUEST_ID = "requestId";
	private static final String BORROWER_VERIFICATION_OTP = "borrowerVerificationOtp";

	public void setCustomerDetails(CreateCustomerRequest customer, CustomerDetails custDtls) {
		PersonalProfile personalProfileToUpdate = custDtls.getPersonalProfile();
		personalProfileToUpdate.setFirstName(customer.getFirstName());
		personalProfileToUpdate.setLastName(customer.getLastName());
		personalProfileToUpdate.setEmailId(customer.getEmailId());
		personalProfileToUpdate.setZip(customer.getZip());
		personalProfileToUpdate.setState(customer.getState());
		personalProfileToUpdate.setDateOfBirth(customer.getDateOfBirth());
		personalProfileToUpdate.setLast4TIN(customer.getLast4TIN());
		personalProfileToUpdate.setAddressLine1(customer.getAddressLine1());
		personalProfileToUpdate.setAddressLine2(customer.getAddressLine2());
		personalProfileToUpdate.setMiddleName(customer.getMiddleName());
//        custDtls.setFinancedAmount(customer.getFinancedAmount());
//        custDtls.setAccountABANumber(customer.getBankABA());
//        custDtls.setSalaryAccountNumber(customer.getBankAccountNumber());
	}

	public CustomerDetails buildCustomerDetails(CreateCustomerRequest customer) {
		PersonalProfile personalProfile = PersonalProfile.builder().firstName(customer.getFirstName())
				.lastName(customer.getLastName()).emailId(customer.getEmailId()).cellPhone(customer.getCellPhone())
				.middleName(customer.getMiddleName()).addressLine1(customer.getAddressLine1())
				.addressLine2(customer.getAddressLine2()).zip(customer.getZip()).city(customer.getCity())
				.state(customer.getState()).last4TIN(customer.getLast4TIN()).dateOfBirth(customer.getDateOfBirth())
				.build();

		CustomerDetails customerEntity = CustomerDetails.builder().personalProfile(personalProfile)
				.firstDateOfPayment(customer.getFirstDateOfPayment())
				.repaymentFrequency(customer.getRepaymentFrequency())
				.numberOfInstallments(customer.getNumberOfInstallments()).checkOutExperience(customer.getCheckOutExperience())
				.installmentAmount(customer.getInstallmentAmount()).build();
//         		.financedAmount(customer.getFinancedAmount()).financedAmount(customer.getFinancedAmount())
//         		.abaOfSalaryAccount(customer.getBankABA()).salaryAccountNumber(customer.getBankAccountNumber()).build();

		return customerEntity;
	}

	public FineractCreateLenderDTO setFineractDataToCreateAccount(CustomerDetails customerEntity,
			String fineractClientType) throws ParseException {
		FineractCreateLenderDTO fineractCreateAccountDTO = new FineractCreateLenderDTO();
		FineractLenderAddressDTO fineractLenderAddressDTO = new FineractLenderAddressDTO();
		Set<FineractLenderAddressDTO> sFineractLenderAddress = new HashSet<FineractLenderAddressDTO>();
		fineractLenderAddressDTO.setAddressTypeId("1");
		fineractLenderAddressDTO.setAddressLine1(customerEntity.getPersonalProfile().getAddressLine1());
		fineractLenderAddressDTO.setIsActive(true);
		fineractLenderAddressDTO.setStateProvinceId(Long.valueOf("1"));
		fineractLenderAddressDTO.setCountryId(Long.valueOf("1"));
		sFineractLenderAddress.add(fineractLenderAddressDTO);
		if (StringUtils.isNotBlank(customerEntity.getPersonalProfile().getFirstName()))
			fineractCreateAccountDTO.setFirstname(customerEntity.getPersonalProfile().getFirstName());
		else
			fineractCreateAccountDTO.setFirstname(customerEntity.getPersonalProfile().getCellPhone());

		if (StringUtils.isNotBlank(customerEntity.getPersonalProfile().getLastName()))
			fineractCreateAccountDTO.setLastname(customerEntity.getPersonalProfile().getLastName());
		else
			fineractCreateAccountDTO.setLastname(customerEntity.getPersonalProfile().getEmailId());
//    	fineractCreateAccountDTO.setFullname(customerEntity.getPersonalProfile().getFirstName()+" "+ customerEntity.getPersonalProfile().getLastName());
		fineractCreateAccountDTO.setExternalId(customerEntity.getPersonalProfile().getCellPhone());
		fineractCreateAccountDTO.setMobileNo(customerEntity.getPersonalProfile().getCellPhone());

		fineractCreateAccountDTO.setDateFormat("dd MMMM yyyy");
		fineractCreateAccountDTO.setLocale("en");
		fineractCreateAccountDTO.setActive(true);
		DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		String requiredDate = df.format(new Date()).toString();
//		if(StringUtils.isNotBlank(customerEntity.getPersonalProfile().getDateOfBirth())) {
//			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
//			Date d1 = df1.parse(customerEntity.getPersonalProfile().getDateOfBirth());
//			String dateOfBirth = df.format(d1);
//			fineractCreateAccountDTO.setDateOfBirth(dateOfBirth);
//		}
		fineractCreateAccountDTO.setActivationDate(requiredDate);
		fineractCreateAccountDTO.setSubmittedOnDate(requiredDate);
		fineractCreateAccountDTO.setOfficeId(Long.valueOf("1"));
		fineractCreateAccountDTO.setClientTypeId(Long.valueOf(fineractClientType));
		fineractCreateAccountDTO.setLegalFormId(Long.valueOf("2"));
		fineractCreateAccountDTO.setSavingsProductId(Long.valueOf("2"));
		fineractCreateAccountDTO.setAddress(sFineractLenderAddress);
		return fineractCreateAccountDTO;

	}

	/**
	 * Method fetches the request details by request ID.
	 * 
	 * @param requestId
	 * @return
	 * @throws ResourceAccessException
	 * @throws GeneralCustomException
	 */
	public RequestIdResponseDTO fetchrequestIdDetails(String requestId, String identifyProviderServiceUri,
			RestTemplate restTemplate) {
		log.info("request id :: " + requestId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(REQUEST_ID, requestId);
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		RequestIdResponseDTO requestIdResponse = new RequestIdResponseDTO();
		try {
			log.info("identifyProviderServiceUri:: " + identifyProviderServiceUri);

			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(identifyProviderServiceUri);
			log.info("uriBuilder url formed:: " + uriBuilder.toUriString());
			requestIdResponse = restTemplate
					.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, RequestIdResponseDTO.class)
					.getBody();

		} catch (ResourceAccessException resourceException) {
			Sentry.captureException(resourceException);
			throw new ServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE.toString(),
					resourceException.getMessage());
		} catch (Exception ex) {
			Sentry.captureException(ex);
			throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
		}
		return requestIdResponse;
	}

	/**
	 * Method communicates with the identity service provider to update request
	 * details by request ID.
	 * 
	 * @param requestId
	 * @return
	 * @throws ResourceAccessException
	 * @throws GeneralCustomException
	 */
	public RequestIdResponseDTO updateRequestIdDetails(String requestId, RequestIdDTO requestIdDTO,
			String identifyProviderServiceUri, RestTemplate restTemplate)
			throws ResourceAccessException, GeneralCustomException, ServiceNotAvailableException {
		log.info("Inside updateRequestIdDetails");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("x-request-id", requestId);
		HttpEntity<String> requestEntity = new HttpEntity(requestIdDTO, headers);

		RequestIdResponseDTO requestIdResponse = new RequestIdResponseDTO();
		try {
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(identifyProviderServiceUri);

			HttpClient httpClient = HttpClientBuilder.create().build();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

			requestIdResponse = restTemplate
					.exchange(uriBuilder.toUriString(), HttpMethod.PATCH, requestEntity, RequestIdResponseDTO.class)
					.getBody();

		} catch (ResourceAccessException resourceException) {
			Sentry.captureException(resourceException);
			throw new ServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE.toString(),
					resourceException.getMessage());
		} catch (Exception ex) {
			Sentry.captureException(ex);
			throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
		}
		log.info("response from  updateRequestIdDetails : " + requestIdResponse);
		return requestIdResponse;
	}

	public RequestIdDTO setRequestIdDetails(CustomerDetails saveCustomer, CallbackURL callbackURL,
			FlowTypeEnum flowType, RequestIdDetails requestIdDetails) {

		RequestIdDTO requestIdDTO = new RequestIdDTO();
		try {
			requestIdDTO.setUserId(saveCustomer.getCustomerId());
			if(saveCustomer.getVirtualAccount() != null && saveCustomer.getVirtualAccount().length() > 0)
				requestIdDTO.setVirtualAccountNumber(saveCustomer.getVirtualAccount());
			else 
				requestIdDTO.setVirtualAccountNumber(saveCustomer.getExternalAccount());
			
			if(saveCustomer.getVirtualAccountId() != null && saveCustomer.getVirtualAccountId().length() > 0)
				requestIdDTO.setVirtualAccountId(saveCustomer.getVirtualAccountId());
			
			if (flowType.name().equals(FlowTypeEnum.DEPOSIT_ALLOCATION.name()))
				requestIdDTO.setDirectDepositAllocation(true);
			else
				requestIdDTO.setDirectDepositAllocation(false);
			
			if(saveCustomer.getAccountABANumber() != null && saveCustomer.getAccountABANumber().length() > 0)
				requestIdDTO.setAbaNumber(saveCustomer.getAccountABANumber());
			else
				requestIdDTO.setAbaNumber(saveCustomer.getExternalAccountABA());
			
			List<FlowTypeEnum> lsFlowType = requestIdDetails.getFlowType();
			if (lsFlowType != null && lsFlowType.size() > 0) {
				if (!lsFlowType.contains(flowType))
					lsFlowType.add(flowType);
			} else {
				lsFlowType = new ArrayList<FlowTypeEnum>();
				lsFlowType.add(flowType);
			}
			requestIdDTO.setFlowType(lsFlowType);
			requestIdDTO.setCurrentFlowType(flowType);
			/* SET CALLBACK URL TO THE REQUEST SERVICE - REQUESTID DETAILS TABLE */
			if (callbackURL != null) {
				if (callbackURL.getIdentityCallbackUrls() != null && callbackURL.getIdentityCallbackUrls().size() > 0)
					requestIdDTO.setIdentityCallbackUrls(callbackURL.getIdentityCallbackUrls());
				else
					requestIdDTO.setIdentityCallbackUrls(requestIdDetails.getIdentityCallbackUrls());

				if (callbackURL.getEmploymentCallbackUrls() != null
						&& callbackURL.getEmploymentCallbackUrls().size() > 0)
					requestIdDTO.setEmploymentCallbackUrls(callbackURL.getEmploymentCallbackUrls());
				else
					requestIdDTO.setEmploymentCallbackUrls(requestIdDetails.getEmploymentCallbackUrls());

				if (callbackURL.getIncomeCallbackUrls() != null && callbackURL.getIncomeCallbackUrls().size() > 0)
					requestIdDTO.setIncomeCallbackUrls(callbackURL.getIncomeCallbackUrls());
				else
					requestIdDTO.setIncomeCallbackUrls(requestIdDetails.getIncomeCallbackUrls());

				if (callbackURL.getAllocationCallbackUrls() != null
						&& callbackURL.getAllocationCallbackUrls().size() > 0)
					requestIdDTO.setAllocationCallbackUrls(callbackURL.getAllocationCallbackUrls());
				else
					requestIdDTO.setAllocationCallbackUrls(requestIdDetails.getAllocationCallbackUrls());

				if (callbackURL.getInsufficientFundCallbackUrls() != null
						&& callbackURL.getInsufficientFundCallbackUrls().size() > 0)
					requestIdDTO.setInsufficientFundCallbackUrls(callbackURL.getInsufficientFundCallbackUrls());
				else
					requestIdDTO.setInsufficientFundCallbackUrls(requestIdDetails.getInsufficientFundCallbackUrls());

				if (callbackURL.getNotificationUrls() != null && callbackURL.getNotificationUrls().size() > 0)
					requestIdDTO.setNotificationUrls(callbackURL.getNotificationUrls());
				else
					requestIdDTO.setNotificationUrls(requestIdDetails.getNotificationUrls());
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			throw new GeneralCustomException("ERROR", "Exception occured while updating the request Id details");
		}
		return requestIdDTO;
	}

	public String getLinkFromLinkVerificationService(String requestId, String domainNameForLink,
			RestTemplate restTemplate, String createLinkUri) {
		log.info("Inside getLinkFromLinkVerificationService");
		LinkRequestProductDTO linkeRequest = new LinkRequestProductDTO();
		linkeRequest.setDomain(domainNameForLink);
		linkeRequest.setLinkType(BORROWER_VERIFICATION_OTP);
		String linkResponse = "";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(LINK_REQUEST_ID, requestId);

		HttpEntity<LinkRequestProductDTO> request = new HttpEntity<>(linkeRequest, headers);

		try {
			linkResponse = restTemplate.postForObject(createLinkUri, request, String.class);
			log.info(" Response from getLinkFromLinkVerificationService : " + linkResponse);
		} catch (Exception ex) {
			Sentry.captureException(ex);
			log.error("link creation failed " + ex.getMessage());
			throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
		}
		log.info("getLinkFromLinkVerificationService response : " + linkResponse);
		return linkResponse;
	}

	/**
	 * Methods that communicates with the account microservice to create a client
	 * and savings account for the customer.
	 * 
	 * @param customerEntity
	 * @return
	 * @throws GeneralCustomException
	 */
	public CustomerDetails createFineractVirtualAccount(String requestId, CustomerDetails customerEntity)
			throws ResourceAccessException, ServiceNotAvailableException, FineractAPIException,
			HttpClientErrorException {
		try {
			/* SET DATA FOR FINERACT API CALL */
			FineractCreateLenderDTO fineractCreateAccountDTO = setFineractDataToCreateAccount(customerEntity,
					fineractClientType);

			/* POST CALL TO ACCOUNT SERVICE TO ACCESS FINERACT API */
			ObjectMapper objMapper = new ObjectMapper();
			HttpEntity<String> requestEnty = new HttpEntity(fineractCreateAccountDTO);
			ResponseEntity<Object> response = (ResponseEntity<Object>) restTemplate
					.postForEntity(createVirtualAccountUri, requestEnty, Object.class);
			FineractLenderCreationResponseDTO fineractAccountCreationresponse = objMapper
					.convertValue(response.getBody(), FineractLenderCreationResponseDTO.class);
			if (fineractAccountCreationresponse != null && fineractAccountCreationresponse.getSavingsId() != null) {
				customerEntity.setVirtualAccount(fineractAccountCreationresponse.getSavingsAccountNumber());
				customerEntity
						.setVirtualAccountId(String.valueOf(fineractAccountCreationresponse.getSavingsId().intValue()));
				customerEntity
						.setVirtualClientId(String.valueOf(fineractAccountCreationresponse.getClientId().intValue()));
				return customerEntity;
			} else
				throw new FineractAPIException("Error while creating virtual savings account for the customer");
		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			throw new FineractAPIException(
					"Error while creating virtual savings account for the customer" + e.getMessage());
		} catch (ResourceAccessException e) {
			Sentry.captureException(e);
			throw new ServiceNotAvailableException(ERROR, e.getMessage());
		} catch (HttpClientErrorException e) {
			Sentry.captureException(e);
			throw new FineractAPIException("Error while creating virtual account with fineract API." + e.getMessage());
		} catch (Exception e) {
			Sentry.captureException(e);
			throw new FineractAPIException(e.getMessage());
		}
	}

	public FineractUpdateLenderResponseDTO updateMobileNoInFineract(String cellPhone, String clientId) {
		log.info("Inside getLinkFromLinkVerificationService");
		FineractUpdateLenderAccountDTO fineractUpdateRequest = new FineractUpdateLenderAccountDTO();
		fineractUpdateRequest.setExternalId(cellPhone);
		fineractUpdateRequest.setMobileNo(cellPhone);
		FineractUpdateLenderResponseDTO fineractUpdateLenderResponseDTO = null;

		try {
			ObjectMapper objMapper = new ObjectMapper();
			HttpEntity<FineractUpdateLenderAccountDTO> requestEntity = new HttpEntity<>(fineractUpdateRequest);
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(updateVirtualAccountUri + clientId);
			log.info("uriBuilder url formed: " + uriBuilder.toUriString());
			fineractUpdateLenderResponseDTO = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.PUT,
					requestEntity, FineractUpdateLenderResponseDTO.class).getBody();
		} catch (Exception ex) {
			Sentry.captureException(ex);
			log.error("Exception occured while updating cellPhone for given client in fineract" + ex.getMessage());
			throw new FineractAPIException(ex.getMessage());
		}
		log.info("updateMobileNoInFineract response : " + fineractUpdateLenderResponseDTO);
		return fineractUpdateLenderResponseDTO;
	}

	/**
	 * This method searches the employer based on provided employerID and updates
	 * the data to request details table.
	 * 
	 * @param employerId
	 * @param requestId
	 * @return
	 */
	public EmployerSearchDetailsDTO getEmployerDetailsBasedOnEmployerId(String employerId, String requestId) {
		log.info("Inside getEmployerDetailsBasedOnEmployerId");
		EmployerSearchDetailsDTO employerSearchDetailsDTO = new EmployerSearchDetailsDTO();
		try {
			ObjectMapper objMapper = new ObjectMapper();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("x-request-id", requestId);
			HttpEntity<String> requestEntity = new HttpEntity<>(headers);

			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(searchEmployerUri + employerId + "?pdSupported=true");
			log.info("uriBuilder url formed: " + uriBuilder.toUriString());
			employerSearchDetailsDTO = restTemplate
					.exchange(uriBuilder.toUriString(), HttpMethod.POST, requestEntity, EmployerSearchDetailsDTO.class)
					.getBody();
		} catch (Exception ex) {
			Sentry.captureException(ex);
			log.error("Exception occured while getting employer details for given employerID" + ex.getMessage());
			throw new GeneralCustomException("ERROR", ex.getMessage());
		}
		log.info("getEmployerDetailsBasedOnEmployerId response : " + employerSearchDetailsDTO);
		return employerSearchDetailsDTO;
	}

	public String createAndSendLinkSMSAndEmailNotification(String requestId, RequestIdDetails requestIdDetails,
			CustomerDetails customerDetails) throws SMSAndEmailNotificationException, GeneralCustomException {
		log.info("Inside createAndSendSMSAndEmailNotification");
		String notificationResponse = "FAIL";
		try {
			String linkResponse = getLinkFromLinkVerificationService(requestId, domainNameForLink, restTemplate,
					createLinkUri);
			notificationResponse = notificationUtil.callNotificationService(requestIdDetails, customerDetails,
					linkResponse);
		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			log.error("Create and send link exception " + e.getMessage());
			throw new SMSAndEmailNotificationException(e.getMessage());
		} catch (Exception e) {
			Sentry.captureException(e);
			log.error("Create and send link exception " + e.getMessage());
			throw new SMSAndEmailNotificationException(e.getMessage());
		}
		log.info("createAndSendSMSAndEmailNotification response : " + notificationResponse);
		return notificationResponse;
	}

	public void validateCustomerRequestFields(CustomerRequestFields customerRequestFields) {
		List<String> errorList = new ArrayList<String>();
		Map<String, List<String>> mapErrorList = new HashMap<String, List<String>>();
		try {
			if (customerRequestFields != null) {
				if (StringUtils.isNotBlank(customerRequestFields.getFirstName())
						&& customerRequestFields.getFirstName().equalsIgnoreCase("NO")) {
					errorList.add(AppConstants.FIRST_NAME_MANDATORY_MESSAGE);
					mapErrorList.put("First Name", errorList);
				}
				if (StringUtils.isNotBlank(customerRequestFields.getLastName())
						&& customerRequestFields.getLastName().equalsIgnoreCase("NO")) {
					errorList.add(AppConstants.LAST_NAME_MANDATORY_MESSAGE);
					mapErrorList.put("Last Name", errorList);
				}
				if (StringUtils.isNotBlank(customerRequestFields.getCellPhone())
						&& customerRequestFields.getCellPhone().equalsIgnoreCase("NO")) {
					errorList.add(AppConstants.CELLPHONE_MANDATORY_MESSAGE);
					mapErrorList.put("CellPhone Number", errorList);
				}
				if (StringUtils.isNotBlank(customerRequestFields.getEmailId())
						&& customerRequestFields.getEmailId().equalsIgnoreCase("NO")) {
					errorList.add(AppConstants.EMAIL_MANDATORY_MESSAGE);
					mapErrorList.put("Email", errorList);
				}
				if (StringUtils.isNotBlank(customerRequestFields.getCallbackURLs())
						&& customerRequestFields.getCallbackURLs().equalsIgnoreCase("NO")) {
					errorList.add(AppConstants.CALLBACKS_MANDATORY_MESSAGE);
					mapErrorList.put("Callback URL", errorList);
				}

				if (mapErrorList.size() > 0) {
					ObjectMapper objectMapper = new ObjectMapper();
					String json = "";
					try {
						json = objectMapper.writeValueAsString(mapErrorList);
						log.error("Mandatory fields can't be made optional - " + json);
					} catch (JsonProcessingException e) {
						Sentry.captureException(e);
						throw new GeneralCustomException(ERROR,
								"Mandatory fields can't be made optional  - " + mapErrorList);
					}
					throw new GeneralCustomException(ERROR, "Mandatory fields can't be made optional  - " + json);
				}
			}
		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			log.error("Mandatory fields can't be made optional - " + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		} catch (Exception e) {
			Sentry.captureException(e);
			log.error("Mandatory fields can't be made optional - " + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		}
	}

	public RequestIdDetails validateRequestId(String requestId, String identifyProviderServiceUri,
			RestTemplate restTemplate, CreateCustomerRequest customer, CustomerRepository customerRepository) {
		RequestIdDetails requestIdDtls = null;
		try {
			RequestIdResponseDTO requestIdResponseDTO = Optional
					.ofNullable(fetchrequestIdDetails(requestId, identifyProviderServiceUri, restTemplate))
					.orElseThrow(() -> new RequestIdNotFoundException("Request Id not found"));
			requestIdDtls = requestIdResponseDTO.getData();

			if (requestIdDtls != null && customer != null) {
				if (StringUtils.isNotBlank(requestIdDtls.getUserId())) {
					Optional<CustomerDetails> optionalCustDetails = customerRepository
							.findByCustomerId(requestIdDtls.getUserId());
					CustomerDetails custDetails = null;
					if (optionalCustDetails.isPresent()) {
						custDetails = optionalCustDetails.get();
					}
					if (custDetails != null) {
						String cellPhone = custDetails.getPersonalProfile().getCellPhone();
						String emailId = custDetails.getPersonalProfile().getEmailId();
						if (!(cellPhone.equalsIgnoreCase(customer.getCellPhone())
								&& emailId.equalsIgnoreCase(customer.getEmailId()))) {
							log.error(
									"Customer already associated with this request Id. CellPhone or EmailId does not match with the customer profile");
							throw new GeneralCustomException(ERROR,
									"Customer already associated with this request Id. CellPhone or EmailId does not match with the customer profile");
						}
					}
				}
				if (StringUtils.isNotBlank(requestIdDtls.getAllocationStatus())) {
					log.error(
							"Customerservice createcustomer - Active/Failed Allocation has already been made for this requestId");
					throw new GeneralCustomException(ERROR,
							"Active/Failed Allocation has already been made for this requestId");
				}
			} else {
				log.error("Exception occured while fetching request Id details");
				throw new GeneralCustomException(ERROR, "Request details does not exist");
			}
		} catch (ServiceNotAvailableException e) {
			Sentry.captureException(e);
			log.error("Exception occured while fetching request Id details- Service unavailable");
			throw new ServiceNotAvailableException(ERROR, e.getMessage());
		} catch (Exception e) {
			Sentry.captureException(e);
			log.error("Exception occured while fetching request Id details");
			throw new GeneralCustomException(ERROR, e.getMessage());
		}
		return requestIdDtls;
	}

	public EmployerInfoResponseDTO getEmployerInfoFromDbById(RequestIdDetails requestIdDetail) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(REQUEST_ID, requestIdDetail.getRequestId());
		HttpEntity<RequestIdDetails> requestEntity = new HttpEntity<>(headers);
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromHttpUrl(employerSearchServicePath + requestIdDetail.getEmployerPWId());

		log.info("Url formed for employer search by id :: " + uriBuilder.toUriString());
		EmployerInfoResponseDTO employerInfo = restTemplate
				.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, EmployerInfoResponseDTO.class)
				.getBody();

		if (null == employerInfo) {
			throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
					"No employer data found for the employer id");
		}
		return employerInfo;
	}

	public boolean  checkIfEmployerPdSuported(RequestIdDetails requestIdDtls) {
		boolean isEmployerPdSupported = false;
		EmployerInfoResponseDTO employerResponseDto = getEmployerInfoFromDbById(requestIdDtls);
		for (ProviderInfo providerInfo : employerResponseDto.getData().getProviderInfo()) {
			log.info("pd support for employer ::" + providerInfo.getPdSupported());
			if (providerInfo.getProviderName().equalsIgnoreCase(requestIdDtls.getProvider())
					&& providerInfo.getPdSupported().equalsIgnoreCase(YES)) {
				log.info("pd support for checked method ::" + providerInfo.getProviderName() + "=========" + "******"
						+ requestIdDtls.getProvider());
				isEmployerPdSupported = true;
			}
		}
		return isEmployerPdSupported;
		
	}

	public Optional<FlowTypeEnum> fetchInProgressRequestFlow(RequestIdDetails requestIdDetails) {
		return Optional.ofNullable(requestIdDetails.getFlowType())
				.orElse(new ArrayList<>())
				.stream()
				.filter(flowTypeEnum -> {
					boolean status = false;
					switch (flowTypeEnum) {
						case DEPOSIT_ALLOCATION:
							status = StringUtils.isBlank(requestIdDetails.getAllocationStatus());
							break;
						case IDENTITY_VERIFICATION:
							status = StringUtils.isBlank(requestIdDetails.getIdentifyStatus());
							break;
						case EMPLOYMENT_VERIFICATION:
							status = StringUtils.isBlank(requestIdDetails.getEmploymentStatus());
							break;
						case INCOME_VERIFICATION:
							status = StringUtils.isBlank(requestIdDetails.getIncomeValidation());
							break;
					}
					return status;
				}).findFirst();
	}
}
