package com.paywallet.userservice.user.services;

import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.entities.PersonalProfile;
import com.paywallet.userservice.user.entities.SalaryProfile;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.FineractCreateLenderDTO;
import com.paywallet.userservice.user.model.FineractLenderAddressDTO;
import com.paywallet.userservice.user.model.LinkRequestProductDTO;
import com.paywallet.userservice.user.model.RequestIdDTO;
import com.paywallet.userservice.user.model.RequestIdResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerServiceHelper {
	
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
//        custDtls.setAbaOfSalaryAccount(customer.getBankABA());
//        custDtls.setSalaryAccountNumber(customer.getBankAccountNumber());
    }

	public CustomerDetails buildCustomerDetails(CreateCustomerRequest customer) {
		PersonalProfile personalProfile = PersonalProfile.builder().firstName(customer.getFirstName())
				.lastName(customer.getLastName()).emailId(customer.getEmailId()).mobileNo(customer.getMobileNo())
				.middleName(customer.getMiddleName()).addressLine1(customer.getAddressLine1())
				.addressLine2(customer.getAddressLine2()).zip(customer.getZip()).city(customer.getCity()).state(customer.getState())
				.last4TIN(customer.getLast4TIN()).dateOfBirth(customer.getDateOfBirth()).build();

		CustomerDetails customerEntity = CustomerDetails.builder().personalProfile(personalProfile).build();
//         		.financedAmount(customer.getFinancedAmount()).financedAmount(customer.getFinancedAmount())
//         		.abaOfSalaryAccount(customer.getBankABA()).salaryAccountNumber(customer.getBankAccountNumber()).build();

		return customerEntity;
	}

	public FineractCreateLenderDTO setFineractDataToCreateAccount(CustomerDetails customerEntity) throws ParseException {
		FineractCreateLenderDTO fineractCreateAccountDTO = new FineractCreateLenderDTO();
		FineractLenderAddressDTO fineractLenderAddressDTO = new FineractLenderAddressDTO();
		Set<FineractLenderAddressDTO> sFineractLenderAddress = new HashSet<FineractLenderAddressDTO>();
		fineractLenderAddressDTO.setAddressTypeId("1");
		fineractLenderAddressDTO.setAddressLine1(customerEntity.getPersonalProfile().getAddressLine1());
		fineractLenderAddressDTO.setIsActive(true);
		fineractLenderAddressDTO.setStateProvinceId(Long.valueOf("1"));
		fineractLenderAddressDTO.setCountryId(Long.valueOf("1"));
		sFineractLenderAddress.add(fineractLenderAddressDTO);
		fineractCreateAccountDTO.setFirstname(customerEntity.getPersonalProfile().getFirstName());
		fineractCreateAccountDTO.setLastname(customerEntity.getPersonalProfile().getLastName());
//    	fineractCreateAccountDTO.setFullname(customerEntity.getPersonalProfile().getFirstName()+" "+ customerEntity.getPersonalProfile().getLastName());
		fineractCreateAccountDTO.setExternalId(customerEntity.getPersonalProfile().getMobileNo());
		fineractCreateAccountDTO.setMobileNo(customerEntity.getPersonalProfile().getMobileNo());

		fineractCreateAccountDTO.setDateFormat("dd MMMM yyyy");
		fineractCreateAccountDTO.setLocale("en");
		fineractCreateAccountDTO.setActive(true);
		DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = df1.parse(customerEntity.getPersonalProfile().getDateOfBirth());
		String requiredDate = df.format(new Date()).toString();

		String dateOfBirth = df.format(d1);
		fineractCreateAccountDTO.setDateOfBirth(dateOfBirth);
		fineractCreateAccountDTO.setActivationDate(requiredDate);
		fineractCreateAccountDTO.setSubmittedOnDate(requiredDate);
		fineractCreateAccountDTO.setOfficeId(Long.valueOf("1"));
		fineractCreateAccountDTO.setClientTypeId(Long.valueOf("17"));
		fineractCreateAccountDTO.setLegalFormId(Long.valueOf("2"));
		fineractCreateAccountDTO.setSavingsProductId(Long.valueOf("2"));
		fineractCreateAccountDTO.setAddress(sFineractLenderAddress);
		return fineractCreateAccountDTO;

	}
	
	/**
     * Method fetches the request details by request ID.
     * @param requestId
     * @return
     * @throws ResourceAccessException
     * @throws GeneralCustomException
     */
    public RequestIdResponseDTO fetchrequestIdDetails(String requestId, String identifyProviderServiceUri, RestTemplate restTemplate) {
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
			throw new ServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE.toString(), resourceException.getMessage());
		} catch (Exception ex) {
			throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),ex.getMessage());
		}
		return requestIdResponse;
	}
    
    /**
     * Method communicates with the identity service provider to update request details by request ID.
     * @param requestId
     * @return
     * @throws ResourceAccessException
     * @throws GeneralCustomException
     */
    public RequestIdResponseDTO updateRequestIdDetails(String requestId, String customerId, String virtualAccountNumber,String identifyProviderServiceUri,
    		RestTemplate restTemplate)  throws ResourceAccessException, GeneralCustomException, ServiceNotAvailableException {
    	log.info("Inside updateRequestIdDetails");
    	
    	/* SET INPUT (REQUESTIDDTO) TO ACCESS THE IDENTITY PROVIDER SERVICE*/
    	RequestIdDTO requestIdDTO = new RequestIdDTO();
    	requestIdDTO.setUserId(customerId);
    	requestIdDTO.setVirtualAccountNumber(virtualAccountNumber);
    	
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("x-request-id", requestId);
		HttpEntity<String> requestEntity = new HttpEntity(requestIdDTO, headers);
		
		RequestIdResponseDTO requestIdResponse = new RequestIdResponseDTO();
		try {
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromHttpUrl(identifyProviderServiceUri);
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
			
			requestIdResponse = restTemplate
					.exchange(uriBuilder.toUriString(), HttpMethod.PATCH, requestEntity, RequestIdResponseDTO.class)
					.getBody();

		} catch (ResourceAccessException resourceException) {
			throw new ServiceNotAvailableException( HttpStatus.SERVICE_UNAVAILABLE.toString(), resourceException.getMessage());
		} catch (Exception ex) {
			throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
		}
		log.info("response from  updateRequestIdDetails : " + requestIdResponse);
		return requestIdResponse;
	}
    
    public String getLinkFromLinkVerificationService(String requestId,String domainNameForLink, RestTemplate restTemplate, String createLinkUri) {
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
			log.error("link creation failed " + ex.getMessage());
			throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),ex.getMessage());
		}
		log.info("getLinkFromLinkVerificationService response : " + linkResponse);
		return linkResponse;
	}

}
