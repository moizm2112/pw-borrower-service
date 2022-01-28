package com.paywallet.userservice.user.services;

import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;
import static com.paywallet.userservice.user.constant.AppConstants.EMAIL_NOTIFICATION_SUCCESS;
import static com.paywallet.userservice.user.constant.AppConstants.EMAIL_NOTIFICATION_FAILED;
import static com.paywallet.userservice.user.constant.AppConstants.SMS_NOTIFICATION_FAILED;
import static com.paywallet.userservice.user.constant.AppConstants.SMS_NOTIFICATION_SUCCESS;


import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.ProviderTypeEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.CustomerAccountException;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.exception.FineractAPIException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.SMSAndEmailNotificationException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.AccountDetails;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.CustomerAccountResponseDTO;
import com.paywallet.userservice.user.model.CustomerResponseDTO;
import com.paywallet.userservice.user.model.FineractCreateLenderDTO;
import com.paywallet.userservice.user.model.FineractLenderCreationResponseDTO;
import com.paywallet.userservice.user.model.LinkRequestProductDTO;
import com.paywallet.userservice.user.model.LyonsAPIRequestDTO;
import com.paywallet.userservice.user.model.OtpProduct;
import com.paywallet.userservice.user.model.RequestIdDTO;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.RequestIdResponseDTO;
import com.paywallet.userservice.user.model.Response;
import com.paywallet.userservice.user.model.UpdateCustomerRequestDTO;
import com.paywallet.userservice.user.model.ValidateAccountRequest;
import com.paywallet.userservice.user.repository.CustomerRepository;
import com.paywallet.userservice.user.util.CustomerServiceUtil;
import com.paywallet.userservice.user.util.NotificationUtil;

import lombok.extern.slf4j.Slf4j;


@Component
@Service
@Slf4j
public class CustomerService {
    @Value("${customer.validation.maxAllowedUpdates}")
    private Integer maxAllowedUpdates;

    private static final String ERROR = "Error";
    private static final String RESULT = "result";
    private static final String STATUS_DESC = "statusDescription";
    private static final String VALID_RTN = "validRtn";
    private static final String ACCEPT = "Accept";
    

    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    CustomerServiceHelper customerServiceHelper;
    
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LyonsService lyonsService;
    
    @Autowired
    NotificationUtil notificationUtil;

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
    
    /**
     * This attribute holds the URI path of the Account service provider 
     * Microservice to create fineract account
     */
    @Value("${createVirtualAccount.eureka.uri}")
    private String createVirtualAccountUri;
    
    /**
     * This attribute holds the URI path of the Identity service provider Microservice
     */
    @Value("${identifyProviderService.eureka.uri}")
	private String identifyProviderServiceUri;
    
    @Value("${link.login.domainname}")
	private String domainNameForLink;
    
    @Value("${create.link.uri}")
	private String createLinkUri;
    
    @Value("${fineract.clienttype}")
	private String fineractClientType;
    
    
    /**
     * Method fetches customer details by mobileNo
     * @param mobileNo
     * @return
     * @throws CustomerNotFoundException
     */
    public CustomerDetails getCustomer(String customerId) throws CustomerNotFoundException {
        log.debug("Inside getCustomer of CustomerService class" + customerId);
        Optional<CustomerDetails> optionalCustDetails = customerRepository.findByCustomerId(customerId);
        CustomerDetails saveCustomer;
        if (optionalCustDetails.isPresent()) {
        	return optionalCustDetails.get();
        }
        else {
        	throw new CustomerNotFoundException("Customer not present with the customerId: "+customerId+" to fetch customer details");
        }
    }
    
    /**
     * Method fetches customer details by mobileNo
     * @param mobileNo
     * @return
     * @throws CustomerNotFoundException
     */
    public CustomerDetails getCustomerByMobileNo(String mobileNo) throws CustomerNotFoundException {
        log.debug("Inside getCustomer of CustomerService class" + mobileNo);
        Optional<CustomerDetails> optionalCustDetails = customerRepository.findByPersonalProfileMobileNo(mobileNo);
        CustomerDetails saveCustomer;
        if (optionalCustDetails.isPresent()) {
        	return optionalCustDetails.get();
        }
        else {
        	throw new CustomerNotFoundException("Customer not present with the mobileNo: "+mobileNo+" to fetch customer details");
        }
    }
    
    /** 
     * Method create a customer with fineract virtual savings account.
     * @param customer
     * @param requestId
     * @return
     * @throws CreateCustomerException
     * @throws GeneralCustomException
     * @throws ServiceNotAvailableException
     * @throws RequestIdNotFoundException
     */
    public CustomerDetails createCustomer(CreateCustomerRequest customer, String requestId) 
    		throws CreateCustomerException, GeneralCustomException, ServiceNotAvailableException, RequestIdNotFoundException, SMSAndEmailNotificationException {
        log.info("Inside createCustomer of CustomerService class");
        if (!customer.getMobileNo().startsWith("+1") && customer.getMobileNo().length()==10)
            customer.setMobileNo("+1".concat(customer.getMobileNo()));

        int virtualAccount = -1;
        CustomerDetails saveCustomer = new CustomerDetails();
        RequestIdDetails requestIdDtls = null;
        try 
        {
        	RequestIdResponseDTO  requestIdResponseDTO = customerServiceHelper.fetchrequestIdDetails(requestId, identifyProviderServiceUri, restTemplate);
        	requestIdDtls = requestIdResponseDTO.getData();
	        Optional<CustomerDetails> byMobileNo = customerRepository.findByPersonalProfileMobileNo(customer.getMobileNo());
	        if(requestIdDtls.getUserId() != null && requestIdDtls.getUserId().length() > 0) {
	        	log.error("Customerservice createcustomer generalCustomException Create customer failed as request id and customer id already exist in database.");
	        	throw new GeneralCustomException(ERROR ,"Create customer failed as request id and customer id already exist in database.");
	        }
	        if (byMobileNo.isPresent()) {
	        	log.info("Exsiting customer with new requestID : " + requestId);
	            saveCustomer = byMobileNo.get();
	            saveCustomer.setRequestId(requestId);
	            saveCustomer.setExistingCustomer(true);
	            if(requestIdDtls.getClientName() != null) 
	            	saveCustomer.setLender(requestIdDtls.getClientName());
	            /* UPDATE REQUEST TABLE with customerID and virtual account from the existing customer information */
	            customerServiceHelper.updateRequestIdDetails(requestId, saveCustomer.getCustomerId(), 
	            		saveCustomer.getVirtualAccount(), identifyProviderServiceUri, restTemplate, customer);
	            
	            /* CREATE AND SEND SMS AND EMAIL NOTIFICATION */
	            String notificationResponse = createAndSendLinkSMSAndEmailNotification(requestId, requestIdResponseDTO.getData(), saveCustomer);
	            
	            /*   CODE TO UPDATE CUSTOMER IF MOBILE NUMBER EXIST */
	            
	            /*log.info("Customer personal profile is getting updated...");
	            CustomerDetails custDtls = byMobileNo.get();
	            customerServiceHelper.setCustomerDetails(customer,custDtls);
	            
	            saveCustomer = customerRepository.save(byMobileNo.get());
	            log.info("Customer personal profile got updated successfully");*/
	            
	
	        } else {
	        	/* CREATE VIRTUAL ACCOUNT IN FINERACT THORUGH ACCOUNT SERVICE*/
	            CustomerDetails customerEntity = createFineractVirtualAccount(customer);
	            log.info("Virtual fineract account created successfully ");
	            
	            if(requestIdDtls.getClientName() != null) 
	            	customerEntity.setLender(requestIdDtls.getClientName());
	            saveCustomer = customerRepository.save(customerEntity);
	            saveCustomer.setRequestId(requestId);
	            saveCustomer.setExistingCustomer(false);
	            
	            /* UPDATE REQUEST TABLE WITH CUSTOMERID AND VIRTUAL ACCOUNT NUMBER */
	            customerServiceHelper.updateRequestIdDetails(requestId, saveCustomer.getCustomerId(), 
	            		saveCustomer.getVirtualAccount(),identifyProviderServiceUri, restTemplate, customer);
	            
	            /* CREATE AND SEND SMS AND EMAIL NOTIFICATION */
	            String notificationResponse = createAndSendLinkSMSAndEmailNotification(requestId, requestIdResponseDTO.getData(), saveCustomer);
	            log.info("Customer got created successfully");
	        }
    	}
        catch(GeneralCustomException e) {
        	log.error("Customerservice createcustomer generalCustomException");
        	throw new GeneralCustomException(ERROR ,e.getMessage());
        }catch(CreateCustomerException e1) {
        	log.error("Customerservice createcustomer createCustomerException");
        	if(virtualAccount != -1)
        		throw new CreateCustomerException(e1.getMessage());
        }
        catch(RequestIdNotFoundException e) {
        	log.error("Customerservice createcustomer RequestIdNotFoundException");
        	throw new RequestIdNotFoundException(e.getMessage());
        }
        catch(ServiceNotAvailableException e) {
        	log.error("Customerservice createcustomer ServiceNotAvailableException");
        	throw new ServiceNotAvailableException(ERROR, e.getMessage());
        }
        catch(FineractAPIException e) {
        	log.error("Customerservice createcustomer FineractAPIException");
        	throw new FineractAPIException(e.getMessage());
        }
        catch(SMSAndEmailNotificationException e) {
        	log.error("Customerservice createcustomer SMSAndEmailNotificationException");
        	throw new SMSAndEmailNotificationException(e.getMessage());
        }
        catch(Exception e) {
        	log.error("Customerservice createcustomer Exception");
        	throw new GeneralCustomException(ERROR ,e.getMessage());
        }
        return saveCustomer;
    }
    
	/**
	 * Methods that communicates with the account microservice to create a client and savings account for the customer.
	 * @param fineractCreateAccountDTO
	 * @return
	 * @throws GeneralCustomException
	 */
	public CustomerDetails createFineractVirtualAccount(CreateCustomerRequest customer) 
			throws ResourceAccessException, ServiceNotAvailableException, FineractAPIException, HttpClientErrorException {
		try {
			/* SET DATA FOR FINERACT API CALL*/
			CustomerDetails customerEntity = customerServiceHelper.buildCustomerDetails(customer);
			FineractCreateLenderDTO fineractCreateAccountDTO = customerServiceHelper.setFineractDataToCreateAccount(customerEntity, fineractClientType);
			
			/* POST CALL TO ACCOUNT SERVICE TO ACCESS FINERACT API*/
			ObjectMapper objMapper= new ObjectMapper();
			HttpEntity<String> requestEnty = new HttpEntity(fineractCreateAccountDTO);
			ResponseEntity<Object> response = (ResponseEntity<Object>) restTemplate.postForEntity(createVirtualAccountUri, requestEnty, Object.class);
			FineractLenderCreationResponseDTO fineractAccountCreationresponse = objMapper.convertValue(response.getBody(), FineractLenderCreationResponseDTO.class);
			if(fineractAccountCreationresponse != null && fineractAccountCreationresponse.getSavingsId() != null) 
			{
				customerEntity.setVirtualAccount(String.valueOf(fineractAccountCreationresponse.getSavingsId().intValue()));
				return customerEntity;
			}
			else 
				throw new FineractAPIException("Error while creating virtual savings account for the customer");
		}
		catch(GeneralCustomException e) {
			throw new FineractAPIException("Error while creating virtual savings account for the customer");
		}
		catch(ResourceAccessException e) {
			throw new ServiceNotAvailableException(ERROR, e.getMessage());
		}
		catch(HttpClientErrorException e) {
			throw new FineractAPIException("Error while creating virtual account with fineract API.");
		}
		catch(Exception e) {
			throw new FineractAPIException(e.getMessage());
		}
	}
	 

    /**
     * Methods gets customer account details by mobileNo.
     * @param mobileNo
     * @return
     * @throws CustomerAccountException
     * @throws CustomerNotFoundException
     */
    public AccountDetails getAccountDetails(String mobileNo) throws CustomerAccountException, CustomerNotFoundException{
        AccountDetails accountDetails = new AccountDetails();
        Optional<CustomerDetails> customerDetails= customerRepository.findByPersonalProfileMobileNo(mobileNo);
        if(customerDetails.isPresent()) {
            if (customerDetails.get().getSalaryProfile()!=null){
                if (CustomerServiceUtil.doesObjectContainField(customerDetails.get().getSalaryProfile(),"salaryAccount")) {
                    /* sending only leading 4 digits of the accNo in response*/
                    if(customerDetails.get().getSalaryProfile().getSalaryAccount()!=null) {
                        Integer salaryAccNoLength = customerDetails.get().getSalaryProfile().getSalaryAccount().length();
                        String leadingFourDigitsOfSalAccNo = customerDetails.get().getSalaryProfile().getSalaryAccount().substring(salaryAccNoLength - 4, salaryAccNoLength);
                        accountDetails.setSalaryAccountNumber(leadingFourDigitsOfSalAccNo);
                    } else {
                        log.debug("Salary AccNo is NULL for the customer with mobileNo: " + mobileNo+" in salary profile");
                        throw new CustomerAccountException("Salary AccNo is NULL for the customer with mobileNo: " + mobileNo);
                    }

                } else {
                    log.debug("Salary AccNo field not present for the customer with mobileNo: " + mobileNo+" in salary profile");
                    throw new CustomerAccountException("Salary AccNo not updated for the customer with mobileNo: " + mobileNo);
                }

                if (CustomerServiceUtil.doesObjectContainField(customerDetails.get().getSalaryProfile(),"aba")){
                    /* sending only leading 4 digits of the abaOfAccNo in response*/
                    if(customerDetails.get().getSalaryProfile().getAba()!=null){
                        Integer abaOfsalaryAccNoLength = customerDetails.get().getSalaryProfile().getAba().length();
                        String leadingFourDigitsOfabaSalAccNo = customerDetails.get().getSalaryProfile().getAba().substring(abaOfsalaryAccNoLength - 4, abaOfsalaryAccNoLength);
                        accountDetails.setAbaOfSalaryAccount(leadingFourDigitsOfabaSalAccNo);
                    } else {
                        log.debug("ABA Of Salary AccNo is NULL for the customer with mobileNo: " + mobileNo+" in salary profile");
                        throw new CustomerAccountException("ABA Of Salary AccNo is NULL for the customer with mobileNo: " + mobileNo);
                    }

                } else {
                    log.debug("ABA of Salary AccNo not updated for the customer with mobileNo: " + mobileNo+" in salary profile");
                    throw new CustomerAccountException("ABA of Salary AccNo not updated for the customer with mobileNo: " + mobileNo);
                }

            } else {
                log.debug("Salary details are not updated for the customer with mobileNo: "+mobileNo);
                throw new CustomerAccountException("Salary details are not updated for the customer with mobileNo: "+mobileNo );
            }

        } else {
            log.debug("Customer not present with the mobileNo: "+mobileNo+" to fetch account details");
            throw new CustomerNotFoundException("Customer not present with the mobileNo: "+mobileNo+" to fetch account details");
        }

        return accountDetails;
    }

    /**
     * Method validates the customer account information against the lyons API
     * @param validateAccountRequest
     * @return
     * @throws CustomerNotFoundException
     * @throws GeneralCustomException
     */
    public CustomerDetails validateAccountRequest(ValidateAccountRequest validateAccountRequest) 
    		throws CustomerNotFoundException, GeneralCustomException{
        log.info("Inside of validateAccountRequest method");
        CustomerDetails updatedCustomer;
        boolean accntAndabaVerification=false;
        boolean incrementCounter = false;
        Optional<CustomerDetails> customerDetails= customerRepository.findByPersonalProfileMobileNo(validateAccountRequest.getMobileNo());
        if(customerDetails.isPresent()) {
            if (customerDetails.get().getUpdateCounter().equals(maxAllowedUpdates)) {
                log.error("Customer validation update attempts reached maximum allowed");
                throw new GeneralCustomException(ERROR, "Customer validation update attempts reached maximum allowed");
            } else {
                if (customerDetails.get().getSalaryProfile().getProvider().equalsIgnoreCase(ProviderTypeEnum.ARGYLE.toString())) {
                    String leadingFourDigitsOfSalAccNo = validateAccountRequest.getSalaryAccountNumber().substring(validateAccountRequest.getSalaryAccountNumber().length() - 4, validateAccountRequest.getSalaryAccountNumber().length());
                    String leadingFourDigitsOfabaNo = validateAccountRequest.getAbaOfSalaryAccount().substring(validateAccountRequest.getAbaOfSalaryAccount().length() - 4, validateAccountRequest.getAbaOfSalaryAccount().length());
                    if (leadingFourDigitsOfSalAccNo.equalsIgnoreCase(customerDetails.get().getSalaryProfile().getSalaryAccount())
                            && leadingFourDigitsOfabaNo.equalsIgnoreCase(customerDetails.get().getSalaryProfile().getAba())) {
                        log.info("provided customer account details are validated with the existing data from the DB");
                        accntAndabaVerification = true;
                    } else {
                        log.warn("provided customer account details ,either Salary AccNo or abaOfSalaryAccNo or not matching with existing details");
                        incrementCounter = true;
                    }

                }
                if (customerDetails.get().getSalaryProfile().getProvider().equalsIgnoreCase(ProviderTypeEnum.ATOMICFI.toString())) {
                    if (validateAccountRequest.getSalaryAccountNumber().equalsIgnoreCase(customerDetails.get().getSalaryProfile().getSalaryAccount())
                            && validateAccountRequest.getAbaOfSalaryAccount().equalsIgnoreCase(customerDetails.get().getSalaryProfile().getAba())) {
                        log.info("provided customer account details are validated with the existing data from the DB");
                        accntAndabaVerification = true;
                    } else {
                        log.warn("provided customer account details ,either Salary AccNo or abaOfSalaryAccNo or not matching with existing details");
                        incrementCounter = true;
                    }
                }
                if (customerDetails.get().getStatus().isEmpty() || !customerDetails.get().getStatus().equalsIgnoreCase(ACCEPT)) {
                    log.info("Lyons Call to validate Account details");
                    JSONObject jsonObject = lyonsService.checkAccountOwnership(LyonsAPIRequestDTO.builder().firstName(customerDetails.get().getPersonalProfile().getFirstName())
                            .lastName(customerDetails.get().getPersonalProfile().getLastName())
                            .accountNumber(validateAccountRequest.getSalaryAccountNumber())
                            .abaNumber(validateAccountRequest.getAbaOfSalaryAccount())
                            .build());
                    log.debug("Lyons Call result: "+jsonObject.toString());
                    if (jsonObject.has(RESULT)) {
                        JSONObject resultObj = jsonObject.getJSONObject(RESULT);
                        if (resultObj.get(VALID_RTN).equals(true)) {
                            log.info("Customer Account details are validated successfully with Lyons");
                            customerDetails.get().setStatus(resultObj.getString(STATUS_DESC));
                        } else {
                            log.error("Customer Account details are validation FAILED with Lyons");
                            incrementCounter = true;
                        }

                    } else {
                        log.error("error in getting the response from Lyons call");
                        incrementCounter = true;
                    }
                }
                if (accntAndabaVerification) {
                    log.info("As account details are validated successfully updating the DB with full SalaryAccNo & ABANo and resetting the counter to 0");
                    customerDetails.get().setSalaryAccountNumber(validateAccountRequest.getSalaryAccountNumber());
                    customerDetails.get().setAbaOfSalaryAccount(validateAccountRequest.getAbaOfSalaryAccount());
                    if (customerDetails.get().getUpdateCounter() != 0) {
                        customerDetails.get().setUpdateCounter(0);
                    }
                }
                if (incrementCounter) {
                    log.info("As validation failed updating the counter by 1");
                    customerDetails.get().setUpdateCounter(customerDetails.get().getUpdateCounter() + 1);
                }

                updatedCustomer = customerRepository.save(customerDetails.get());

            }
             return updatedCustomer;

        } else {
            throw new CustomerNotFoundException("Customer not exists with the mobileNo: "+validateAccountRequest.getMobileNo()+" to validate");
        }
    }

    /**
     * Method updates the customer salary profile
     * @param updateCustomerRequest
     * @return
     * @throws CustomerNotFoundException
     */
    public CustomerDetails updateCustomerDetails(UpdateCustomerRequestDTO updateCustomerRequest) throws CustomerNotFoundException{

        Optional<CustomerDetails> customerDetailsByMobileNo= customerRepository.findByPersonalProfileMobileNo(updateCustomerRequest.getMobileNo());

        if(customerDetailsByMobileNo.isPresent()){
            log.debug("Customer with the mobile no " + customerDetailsByMobileNo.get().getPersonalProfile().getMobileNo() + " already exists");
            log.info("Customer details are getting updated...");

            if (updateCustomerRequest.getSalaryProfile()!=null)
                customerDetailsByMobileNo.get().setSalaryProfile(updateCustomerRequest.getSalaryProfile());
            log.info("Customer details are updated successfully");
            return customerRepository.save(customerDetailsByMobileNo.get());
        } else {
            log.error("Customer do not exists with the mobileNo: "+updateCustomerRequest.getMobileNo()+" to update");
            throw new CustomerNotFoundException("Customer do not exists with the mobileNo: "+updateCustomerRequest.getMobileNo()+" to update");
        }
    }
    
    /** Method creates a response DTO to orchestrate back to the caller. 
     * This shares the response of customer details, status of request and URI path.
     * @param customerDetails
     * @param message
     * @param status
     * @param path
     * @return
     */
    public CustomerResponseDTO prepareResponseDTO(CustomerDetails customerDetails, String message, int status, String path) {
        return CustomerResponseDTO.builder()
                .data(customerDetails)
                .message(message)
                .status(status)
                .timeStamp(new Date())
                .path(path)
                .requestId(customerDetails.getRequestId())
                .build();
    }
    
    /** Method creates a response DTO to orchestrate back to the caller. 
     * This shares the response of customer details, status of request and URI path.
     * @param customerDetails
     * @param message
     * @param status
     * @param path
     * @return
     */
    public ResponseEntity<Object> prepareResponse(CustomerDetails customerDetails, String message, int status, String path) {
    	
    	Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", customerDetails);
        body.put("message", message);
        body.put("status", status);
        body.put("timestamp", new Date());
        body.put("path", path);
        body.put("requestId", customerDetails.getRequestId());
        if(customerDetails.isEmailNotificationSuccess())
        	body.put("Email Notification", EMAIL_NOTIFICATION_SUCCESS);
        else
        	body.put("Email Notification", customerDetails.getPersonalProfile().getEmailId() + " - " + EMAIL_NOTIFICATION_FAILED);
        if(customerDetails.isSmsNotificationSuccess())
        	body.put("SMS Notification", SMS_NOTIFICATION_SUCCESS);
        else
        	body.put("SMS Notification", customerDetails.getPersonalProfile().getMobileNo() + " - " + SMS_NOTIFICATION_FAILED);
        
        if(status == 201) {
        	return new ResponseEntity<>(body, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
    
    /**
     * Method creates a response DTO to orchestrate back to the caller. 
     * This shares the response of customer Account details, status of request and URI path.
     * @param accountDetails
     * @param message
     * @param status
     * @param path
     * @return
     */
    public CustomerAccountResponseDTO prepareAccountDetailsResponseDTO(AccountDetails accountDetails, String message, int status, String path) {
        return CustomerAccountResponseDTO.builder()
                .data(accountDetails)
                .message(message)
                .status(status)
                .timeStamp(new Date())
                .path(path)
                .build();
    }
    
    
   public String createAndSendLinkSMSAndEmailNotification(String requestId, RequestIdDetails requestIdDetails, CustomerDetails customerDetails) 
		   throws SMSAndEmailNotificationException, GeneralCustomException {
	   log.info("Inside createAndSendSMSAndEmailNotification");
	   String notificationResponse = "FAIL";
	   try {
		   String linkResponse  = customerServiceHelper.getLinkFromLinkVerificationService(requestId, domainNameForLink, restTemplate, createLinkUri);
		   notificationResponse = notificationUtil.callNotificationService(requestIdDetails, customerDetails, linkResponse);
	   }
	   catch(GeneralCustomException e) {
		   log.error("Create and send link exception " + e.getMessage());
			throw new SMSAndEmailNotificationException(e.getMessage());
	   }
	   catch(Exception e) {
		   log.error("Create and send link exception " + e.getMessage());
			throw new SMSAndEmailNotificationException(e.getMessage());
	   }
	   log.info("createAndSendSMSAndEmailNotification response : " + notificationResponse);
	   return notificationResponse;
   }

}

