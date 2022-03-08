package com.paywallet.userservice.user.services;

import static com.paywallet.userservice.user.constant.AppConstants.EMAIL_NOTIFICATION_FAILED;
import static com.paywallet.userservice.user.constant.AppConstants.EMAIL_NOTIFICATION_SUCCESS;
import static com.paywallet.userservice.user.constant.AppConstants.SMS_NOTIFICATION_FAILED;
import static com.paywallet.userservice.user.constant.AppConstants.SMS_NOTIFICATION_SUCCESS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paywallet.userservice.user.constant.AppConstants;
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
import com.paywallet.userservice.user.model.CustomerRequestFields;
import com.paywallet.userservice.user.model.CustomerResponseDTO;
import com.paywallet.userservice.user.model.LyonsAPIRequestDTO;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.RequestIdResponseDTO;
import com.paywallet.userservice.user.model.UpdateCustomerDetailsResponseDTO;
import com.paywallet.userservice.user.model.UpdateCustomerEmailIdDTO;
import com.paywallet.userservice.user.model.UpdateCustomerMobileNoDTO;
import com.paywallet.userservice.user.model.UpdateCustomerRequestDTO;
import com.paywallet.userservice.user.model.ValidateAccountRequest;
import com.paywallet.userservice.user.repository.CustomerRepository;
import com.paywallet.userservice.user.repository.CustomerRequestFieldsRepository;
import com.paywallet.userservice.user.util.CustomerServiceUtil;
import com.paywallet.userservice.user.util.NotificationUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;

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
    CustomerRequestFieldsRepository customerRequestFieldsRepository; 
    
    @Autowired
    CustomerServiceHelper customerServiceHelper;
    
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LyonsService lyonsService;
    
    @Autowired
    NotificationUtil notificationUtil;

    @Autowired
    RequestIdUtil requestIdUtil;
    
    @Autowired
    CustomerFieldValidator customerFieldValidator;

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
    
    @Value("${createVirtualAccount.eureka.uri}")
    private String createVirtualAccountUri;

   /* @Autowired
    KafkaPublisherUtil kafkaPublisherUtil;

    @Autowired
    LinkServiceUtil linkServiceUtil;*/
    
    /**
     * Method fetches customer details by mobileNo
     * @param customerId
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
        if (!mobileNo.startsWith("+1") && mobileNo.length()==10)
        	mobileNo = "+1".concat(mobileNo);
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
//        	RequestIdResponseDTO  requestIdResponseDTO = customerServiceHelper.fetchrequestIdDetails(requestId, identifyProviderServiceUri, restTemplate);
//        	requestIdDtls = requestIdResponseDTO.getData();
//	        if(requestIdDtls.getUserId() != null && requestIdDtls.getUserId().length() > 0) {
//	        	log.error("Customerservice createcustomer generalCustomException Create customer failed as request id and customer id already exist in database.");
//	        	throw new GeneralCustomException(ERROR ,"Create customer failed as request id and customer id already exist in database.");
//	        }
        	requestIdDtls = validateRequestId(requestId, identifyProviderServiceUri, restTemplate);
        	
        	validateCreateCustomerRequest(customer, requestId, requestIdDtls.getClientName());
          
        	if(customer.getTotalNoOfRepayment() == null)
        		customer.setTotalNoOfRepayment(0);
        	if(customer.getInstallmentAmount() ==null)
        		customer.setInstallmentAmount(0);
           checkAndSavePayAllocation(requestIdDtls,customer);


	        Optional<CustomerDetails> byMobileNo = customerRepository.findByPersonalProfileMobileNo(customer.getMobileNo());
	        if (byMobileNo.isPresent()) {
	        	log.info("Exsiting customer with new requestID : " + requestId);
	            saveCustomer = byMobileNo.get();
	            saveCustomer.setRequestId(requestId);
	            saveCustomer.setExistingCustomer(true);
	            if(requestIdDtls.getClientName() != null) 
	            	saveCustomer.setLender(requestIdDtls.getClientName());
	            /* UPDATE REQUEST TABLE with customerID and virtual account from the existing customer information */
	            customerServiceHelper.updateRequestIdDetails(requestId, saveCustomer.getCustomerId(), 
	            saveCustomer.getVirtualAccount(), saveCustomer.getVirtualAccountId(), identifyProviderServiceUri, restTemplate, customer);
	            
	            /* CREATE AND SEND SMS AND EMAIL NOTIFICATION */
	           // String notificationResponse = createAndSendLinkSMSAndEmailNotification(requestId, requestIdDtls, saveCustomer);
	           kafkaPublisherUtil.publishLinkServiceInfo(requestIdDtls,saveCustomer,customer.getInstallmentAmount());
	        } else {
	        	/* CREATE VIRTUAL ACCOUNT IN FINERACT THORUGH ACCOUNT SERVICE*/
	            CustomerDetails customerEntity = customerServiceHelper.createFineractVirtualAccount(requestIdDtls.getRequestId(),customer);
	            log.info("Virtual fineract account created successfully ");
	            
	            if(requestIdDtls.getClientName() != null) 
	            	customerEntity.setLender(requestIdDtls.getClientName());
	            saveCustomer = customerRepository.save(customerEntity);
	            saveCustomer.setRequestId(requestId);
	            saveCustomer.setExistingCustomer(false);
	            
	            /* UPDATE REQUEST TABLE WITH CUSTOMERID AND VIRTUAL ACCOUNT NUMBER */
	            customerServiceHelper.updateRequestIdDetails(requestId, saveCustomer.getCustomerId(), 
	            		saveCustomer.getVirtualAccount(), saveCustomer.getVirtualAccountId(),identifyProviderServiceUri, restTemplate, customer);
	            /* CREATE AND SEND SMS AND EMAIL NOTIFICATION */
	            //String notificationResponse = createAndSendLinkSMSAndEmailNotification(requestId, requestIdDtls, saveCustomer);
                kafkaPublisherUtil.publishLinkServiceInfo(requestIdDtls,saveCustomer,customer.getInstallmentAmount());
	            log.info("Customer got created successfully");
	        }
            checkAndSavePayAllocation(requestIdDtls,customer);
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
    
    /**
     * Method updates the customer Basic Details
     * @param UpdateCustomerMobileNoDTO
     * @return
     * @throws CustomerNotFoundException
     */
    public UpdateCustomerDetailsResponseDTO updateCustomerMobileNo(UpdateCustomerMobileNoDTO updateCustomerMobileNoDTO, String requestId) 
    		throws CustomerNotFoundException, RequestIdNotFoundException{
    	CustomerDetails custDetails = new CustomerDetails();
    	boolean isMobileNoUpdatedInFineract = false;
    	boolean isMobileNoUpdatedInCustomerDetails = false;
    	UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponseDTO = new UpdateCustomerDetailsResponseDTO();
    	try {
    		
    		if(!updateCustomerMobileNoDTO.getMobileNo().startsWith("+1") && updateCustomerMobileNoDTO.getMobileNo().length()==10) {
    			updateCustomerMobileNoDTO.setMobileNo("+1".concat(updateCustomerMobileNoDTO.getMobileNo()));
    		}
    		if(!updateCustomerMobileNoDTO.getNewMobileNo().startsWith("+1") && updateCustomerMobileNoDTO.getNewMobileNo().length()==10) {
    			updateCustomerMobileNoDTO.setNewMobileNo("+1".concat(updateCustomerMobileNoDTO.getNewMobileNo()));
    		}
    		if(updateCustomerMobileNoDTO.getMobileNo().equalsIgnoreCase(updateCustomerMobileNoDTO.getNewMobileNo())) {
    			log.error("Please provide different mobile number. You cannot enter mobile number matching the customer data");
                throw new CustomerNotFoundException("Please provide different mobile number. You cannot enter mobile number matching the customer data");
    		}
    		
    		RequestIdResponseDTO requestIdResponseDTO = Optional.ofNullable(customerServiceHelper.fetchrequestIdDetails(requestId, identifyProviderServiceUri, restTemplate))
        			.orElseThrow(() -> new RequestIdNotFoundException("Request Id not found"));
        	RequestIdDetails requestIdDtls = requestIdResponseDTO.getData();
        	if(StringUtils.isBlank(requestIdDtls.getUserId())) {
        		throw new CustomerNotFoundException("RequestId and mobileNo does not match. Please provide a valid requestId or mobileNo to update");
        	}
        	if(StringUtils.isAllBlank(requestIdDtls.getAllocationStatus())) {
//	        	if( ((String) requestIdDtls.getEmployer()).equalsIgnoreCase(updateCustomerMobileNoDTO.getEmployerName())) {
	        		Optional<CustomerDetails> customerDetailsByMobileNo= customerRepository.findByPersonalProfileMobileNo(updateCustomerMobileNoDTO.getMobileNo());
	                if(customerDetailsByMobileNo.isPresent()){
	                	custDetails = customerDetailsByMobileNo.get();
	                	if(requestIdDtls.getUserId().equalsIgnoreCase(custDetails.getCustomerId())) {
		                	Optional<CustomerDetails> checkForMobileNumberinDB= customerRepository.findByPersonalProfileMobileNo(updateCustomerMobileNoDTO.getNewMobileNo());
			                if(!checkForMobileNumberinDB.isPresent()){
			                	
			                    log.debug("Customer with the mobile no " + custDetails.getPersonalProfile().getMobileNo() + " already exists");
			                    log.info("Customer details are getting updated...");
			                    
			                    
			                    if(!custDetails.getPersonalProfile().getMobileNo().equalsIgnoreCase(updateCustomerMobileNoDTO.getMobileNo())) {
			                    	log.error("Customer does nor exist for the given mobileNumber");
			                        throw new CustomerNotFoundException("Provided mobile number does not match the customer data. Please provide a valid mobile number.");
			                    }
			                    
			                    if(custDetails.getPersonalProfile().getMobileNo().equalsIgnoreCase(updateCustomerMobileNoDTO.getNewMobileNo())) {
			                    	log.error("Updating mobile Number should be different from existing mobile number");
			                        throw new CustomerNotFoundException("Updating Mobile Number (" + updateCustomerMobileNoDTO.getNewMobileNo() +") should be different from existing mobile number");
			                    }
			                    
			                	// Make an fineract call to update the external Id and mobileNo.
			                    customerServiceHelper.updateMobileNoInFineract(updateCustomerMobileNoDTO.getNewMobileNo(), custDetails.getVirtualClientId());
			                    isMobileNoUpdatedInFineract = true;
			                	//Update the Customer table
			                	custDetails.getPersonalProfile().setMobileNo(updateCustomerMobileNoDTO.getNewMobileNo());
			                	custDetails.setRequestId(requestId);
			                	
			                	updateCustomerDetailsResponseDTO.setRequestId(requestId);
			                	updateCustomerDetailsResponseDTO.setMobileNo(updateCustomerMobileNoDTO.getNewMobileNo());
			                	updateCustomerDetailsResponseDTO.setCustomerId(custDetails.getCustomerId());
			                	
			                	log.info("Customer mobile number updated successfully");
			                	custDetails = customerRepository.save(custDetails);
			                	isMobileNoUpdatedInCustomerDetails = true;
			                }
			                else {
			                	log.error("Updating mobile number "+updateCustomerMobileNoDTO.getNewMobileNo()+" exist in database");
			                    throw new CustomerNotFoundException("Updating mobile number "+updateCustomerMobileNoDTO.getNewMobileNo()+" exist in database");
			                }
	                	}
	                	else {
		                	log.error("RequestId and mobileNo does not match. Please provide a valid requestId or mobileNo to update");
		                    throw new CustomerNotFoundException("RequestId and mobileNo does not match. Please provide a valid requestId or mobileNo to update");
		                }
	                } else {
	                    log.error("Customer do not exists with the mobile number: "+updateCustomerMobileNoDTO.getMobileNo()+" to update");
	                    throw new CustomerNotFoundException("Customer do not exists with the mobile number: "+updateCustomerMobileNoDTO.getMobileNo()+" to update");
	                }
//	        	}
//	        	else {
//	        		log.error("Employer name doesn't match with the existing customer details");
//	                throw new GeneralCustomException(ERROR, "Employer name doesn't match with the existing customer details "+updateCustomerMobileNoDTO.getEmployerName()+" to update");
//	        	}
        	}
        	else {
        		log.error("Mobile Number cannot be updated as pay allocation has already been completed");
                throw new CustomerNotFoundException("Mobile Number cannot be updated as pay allocation has already been completed");
        	}
    	}catch(FineractAPIException e) {
    		log.error("Exception occured in fineract while updating mobile number for given client "+ e.getMessage());
    		throw new FineractAPIException(e.getMessage());
    	}catch(CustomerNotFoundException e) {
    		if(isMobileNoUpdatedInFineract && !isMobileNoUpdatedInCustomerDetails)
    			customerServiceHelper.updateMobileNoInFineract(updateCustomerMobileNoDTO.getMobileNo(), custDetails.getVirtualClientId());
    		log.error("Exception occured while updating customer details " + e.getMessage());
    		throw new CustomerNotFoundException(e.getMessage());
    	}catch(GeneralCustomException e) {
    		if(isMobileNoUpdatedInFineract && !isMobileNoUpdatedInCustomerDetails)
    			customerServiceHelper.updateMobileNoInFineract(updateCustomerMobileNoDTO.getMobileNo(), custDetails.getVirtualClientId());
    		log.error("Exception occured while updating customer details " + e.getMessage());
    		throw new GeneralCustomException(ERROR, e.getMessage());
    	}
    	catch(Exception e) {
    		if(isMobileNoUpdatedInFineract && !isMobileNoUpdatedInCustomerDetails)
    			customerServiceHelper.updateMobileNoInFineract(updateCustomerMobileNoDTO.getMobileNo(), custDetails.getVirtualClientId());
    		log.error("Exception occured while updating customer details " + e.getMessage());
    		throw new GeneralCustomException(ERROR, e.getMessage());
    	}
    	return updateCustomerDetailsResponseDTO;
    }
    
    
    /**
     * Method updates the customer Basic Details
     * @param UpdateCustomerMobileNoDTO
     * @return
     * @throws CustomerNotFoundException
     */
    public UpdateCustomerDetailsResponseDTO updateCustomerEmailId(UpdateCustomerEmailIdDTO updateCustomerEmailIdDTO, String requestId) throws CustomerNotFoundException, RequestIdNotFoundException{
    	CustomerDetails custDetails = new CustomerDetails();
    	UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponseDTO = new UpdateCustomerDetailsResponseDTO();
    	try {
    		
    		if(!updateCustomerEmailIdDTO.getMobileNo().startsWith("+1") && updateCustomerEmailIdDTO.getMobileNo().length()==10)
    			updateCustomerEmailIdDTO.setMobileNo("+1".concat(updateCustomerEmailIdDTO.getMobileNo()));
    		
    		if(updateCustomerEmailIdDTO.getEmailId().equalsIgnoreCase(updateCustomerEmailIdDTO.getNewEmailId())) {
            	log.error("You cannot enter exactly same emailId to update");
                throw new CustomerNotFoundException("You cannot enter exactly same emailId to update");
            }
    		
    		RequestIdResponseDTO requestIdResponseDTO = Optional.ofNullable(customerServiceHelper.fetchrequestIdDetails(requestId, identifyProviderServiceUri, restTemplate))
        			.orElseThrow(() -> new RequestIdNotFoundException("Request Id not found"));
        	RequestIdDetails requestIdDtls = requestIdResponseDTO.getData();
        	if(StringUtils.isBlank(requestIdDtls.getUserId())) {
        		throw new CustomerNotFoundException("RequestId and mobileNo does not match. Please provide a valid requestId or mobileNo to update");
        	}
        	if(StringUtils.isAllBlank(requestIdDtls.getAllocationStatus())) { 
//        		if (((String) requestIdDtls.getEmployer()).equalsIgnoreCase(updateCustomerEmailIdDTO.getEmployerName())) {
	        		Optional<CustomerDetails> customerDetailsByMobileNo= customerRepository.findByPersonalProfileMobileNo(updateCustomerEmailIdDTO.getMobileNo());
	                if(customerDetailsByMobileNo.isPresent()) {
	                	custDetails = customerDetailsByMobileNo.get();
	                	if(requestIdDtls.getUserId().equalsIgnoreCase(custDetails.getCustomerId())) {
	                		if(custDetails.getPersonalProfile().getEmailId().equalsIgnoreCase(updateCustomerEmailIdDTO.getEmailId())) {
			                	Optional<CustomerDetails> checkForEmailIdInDB= customerRepository.findByPersonalProfileEmailId(updateCustomerEmailIdDTO.getNewEmailId());
				                if(!checkForEmailIdInDB.isPresent()) {
				                	
				                    log.debug("Customer with the mobile no " + custDetails.getPersonalProfile().getMobileNo() + " already exists");
				                    log.info("Customer details are getting updated...");
				                    
			                		if(!custDetails.getPersonalProfile().getEmailId().equalsIgnoreCase(updateCustomerEmailIdDTO.getEmailId())) {
				                    	log.error("Customer does not exist for the given emailId");
				                        throw new CustomerNotFoundException("Customer does not exist for the provided emailId (" + updateCustomerEmailIdDTO.getEmailId() +")");
			                		}
			                		
			                		if(custDetails.getPersonalProfile().getEmailId().equalsIgnoreCase(updateCustomerEmailIdDTO.getNewEmailId()))  {
			                    		log.error("Updating EmailId should be different from existing emailId");
				                        throw new CustomerNotFoundException("EmailId (" + updateCustomerEmailIdDTO.getNewEmailId() +") should be different from existing emailId");
				                    }
				                    
				                    if(custDetails.getPersonalProfile().getEmailId().equalsIgnoreCase(updateCustomerEmailIdDTO.getEmailId())) {
				                    	custDetails.getPersonalProfile().setEmailId(updateCustomerEmailIdDTO.getNewEmailId());
					                	log.info("Customer Email Id updated successfully");
					                	custDetails =  customerRepository.save(custDetails);
					                	custDetails.setRequestId(requestId);
					                	updateCustomerDetailsResponseDTO.setRequestId(requestId);
					                	updateCustomerDetailsResponseDTO.setMobileNo(updateCustomerEmailIdDTO.getMobileNo());
					                	updateCustomerDetailsResponseDTO.setEmailId(updateCustomerEmailIdDTO.getNewEmailId());
					                	updateCustomerDetailsResponseDTO.setCustomerId(custDetails.getCustomerId());
				                    }
				                    else {
				        				log.error("EmailId do not match with the existing customer details");
				                        throw new CustomerNotFoundException("EmailId (" + updateCustomerEmailIdDTO.getEmailId() +") do not match with the existing customer details");
				        			}
				                }
				                else {
				                	log.error("Updating Email "+updateCustomerEmailIdDTO.getNewEmailId()+" exist in database. Please provide different email");
				                    throw new CustomerNotFoundException("Updating Email "+updateCustomerEmailIdDTO.getNewEmailId()+" exist in database. Please provide different email");
				                }
	                		}
	                		else {
	                			log.error("Provided email doesn't match with the customer's email");
			                    throw new CustomerNotFoundException("\"Provided email "+updateCustomerEmailIdDTO.getEmailId()+" doesn't match with the customer's email.");
	                		}
	                	}
	                	else {
	                		log.error("RequestId and mobileNo does not match. Please provide a valid requestId or mobileNo to update");
		                    throw new CustomerNotFoundException("RequestId and mobileNo does not match. Please provide a valid requestId or mobileNo to update");
		                }
	                } else {
	                    log.error("Customer do not exists with the mobileNo: "+updateCustomerEmailIdDTO.getMobileNo()+" to update");
	                    throw new CustomerNotFoundException("Customer do not exists with the mobileNo: "+updateCustomerEmailIdDTO.getMobileNo()+" to update");
	                }
//        		}
//    			else {
//    				log.error("mployer Name do not match with the existing customer details");
//                    throw new CustomerNotFoundException("Employer Name (" + updateCustomerEmailIdDTO.getEmployerName() + ") do not match with the existing customer details");
//    			}
	                
	                
        	}
        	else {
        		log.error("Email Id cannot be updated as pay allocation has already been completed");
                throw new CustomerNotFoundException("Email Id cannot be updated as pay allocation has already been completed");
        	}
    	}catch(CustomerNotFoundException e) {
    		log.error("Exception occured while updating emailId customer details " + e.getMessage());
    		throw new CustomerNotFoundException(e.getMessage());
    	}catch(GeneralCustomException e) {
    		log.error("Exception occured while updating emailIdcustomer details " + e.getMessage());
    		throw new GeneralCustomException(ERROR, e.getMessage());
    	}
    	catch(Exception e) {
    		if(e.getMessage().contains("returned non unique result")) {
    			log.error("Updating Email "+updateCustomerEmailIdDTO.getNewEmailId()+" exist in database. Please provide different email");
                throw new CustomerNotFoundException("Updating Email "+updateCustomerEmailIdDTO.getNewEmailId()+" exist in database. Please provide different email");
    		}
    		else {
	    		log.error("Exception occured while updating emailId customer details " + e.getMessage());
	    		throw new GeneralCustomException(ERROR, e.getMessage());
    		}
    	}
    	return updateCustomerDetailsResponseDTO;
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
    public ResponseEntity<Object> prepareUpdateResponse(UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponseDTO, String message, int status, String path) {
        
    	Map<String, Object> body = new LinkedHashMap<>();
    	body.put("data", updateCustomerDetailsResponseDTO);
        body.put("message", message);
        body.put("status", status);
        body.put("timestamp", new Date());
        body.put("path", path);
    	return new ResponseEntity<>(body, HttpStatus.OK);
    }
    
    /** Method creates a response DTO to orchestrate back to the caller. 
     * This shares the response of customer details, status of request and URI path.
     * @param customerDetails
     * @param message
     * @param status
     * @param path
     * @return
     */
    public ResponseEntity<Object> prepareResponse(CustomerDetails customerDetails, String message,int status, String path) {
    	
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
   
   public void validateCreateCustomerRequest(CreateCustomerRequest customerRequest, String requestId, String lender){
	   Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
	   try {
		   Optional<CustomerRequestFields> optionalCustomerRequestFields = customerRequestFieldsRepository.findByLender(lender);
		   if(optionalCustomerRequestFields.isPresent()) {
			   CustomerRequestFields customerRequestFields = Optional.ofNullable(optionalCustomerRequestFields.get())
					   .orElseThrow(()-> new GeneralCustomException(ERROR, "Exception occured while fetching required fields for employer"));
			   if("YES".equalsIgnoreCase(customerRequestFields.getFirstName()) || StringUtils.isNotBlank(customerRequest.getFirstName())) 
			   {
				   List<String> errorList = customerFieldValidator.validateFirstName(customerRequest.getFirstName());
				   if(errorList.size() > 0)
					   mapErrorList.put("First Name", errorList);
			   } 
			   if("YES".equalsIgnoreCase(customerRequestFields.getLastName()) || StringUtils.isNotBlank(customerRequest.getLastName())) {
				   List<String> errorList = customerFieldValidator.validateLastName(customerRequest.getLastName());
				   if(errorList.size() > 0)
					   mapErrorList.put("Last Name", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getMobileNo()) || StringUtils.isNotBlank(customerRequest.getMobileNo())) {
				   List<String> errorList = customerFieldValidator.validateMobileNo(customerRequest.getMobileNo());
				   if(errorList.size() > 0)
					   mapErrorList.put("Mobile Number", errorList);
			   }
			   
			   if("YES".equalsIgnoreCase(customerRequestFields.getMiddleName()) || StringUtils.isNotBlank(customerRequest.getMiddleName())) {
				   List<String> errorList = customerFieldValidator.validateMiddleName(customerRequest.getMiddleName());
				   if(errorList.size() > 0)
					   mapErrorList.put("Middle Name", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getAddressLine1()) || StringUtils.isNotBlank(customerRequest.getAddressLine1())) {
				   List<String> errorList = customerFieldValidator.validateAddressLine1(customerRequest.getAddressLine1());
				   if(errorList.size() > 0)
					   mapErrorList.put("Address Line1", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getAddressLine2()) || StringUtils.isNotBlank(customerRequest.getAddressLine2())) {
				   List<String> errorList = customerFieldValidator.validateAddressLine2(customerRequest.getAddressLine2());
				   if(errorList.size() > 0)
					   mapErrorList.put("Address Line2", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getCity()) || StringUtils.isNotBlank(customerRequest.getCity())) {
				   List<String> errorList = customerFieldValidator.validateCity(customerRequest.getCity());
				   if(errorList.size() > 0)
					   mapErrorList.put("City", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getState()) || StringUtils.isNotBlank(customerRequest.getState())) {
				   List<String> errorList = customerFieldValidator.validateState(customerRequest.getState());
				   if(errorList.size() > 0)
					   mapErrorList.put("State", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getZip()) || StringUtils.isNotEmpty(customerRequest.getZip())) {
				   List<String> errorList = customerFieldValidator.validateZip(customerRequest.getZip());
				   if(errorList.size() > 0)
					   mapErrorList.put("Zip", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getLast4TIN()) || StringUtils.isNotBlank(customerRequest.getLast4TIN())) {
				   List<String> errorList = customerFieldValidator.validateLast4TIN(customerRequest.getLast4TIN());
				   if(errorList.size() > 0)
					   mapErrorList.put("Last 4TIN", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getDateOfBirth()) || StringUtils.isNotBlank(customerRequest.getDateOfBirth())) {
				   List<String> errorList = customerFieldValidator.validateDateOfBirth(customerRequest.getDateOfBirth());
				   if(errorList.size() > 0)
					   mapErrorList.put("Date Of Birth", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getEmailId()) || StringUtils.isNotBlank(customerRequest.getEmailId())) {
				   List<String> errorList = customerFieldValidator.validateEmailId(customerRequest.getEmailId(), customerRepository, customerRequest.getMobileNo());
				   if(errorList.size() > 0)
					   mapErrorList.put("EmailId", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getCallbackURLs()) || customerRequest.getCallbackURLs() != null) {
				   List<String> errorList = customerFieldValidator.validateCallbackURLs(customerRequest.getCallbackURLs(), restTemplate , requestId, lender);
				   if(errorList.size() > 0)
					   mapErrorList.put("Callback URLS", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getFirstDateOfPayment()) || StringUtils.isNotBlank(customerRequest.getFirstDateOfPayment())) {
				   List<String> errorList = customerFieldValidator.validateFirstDateOfPayment(customerRequest.getFirstDateOfPayment(), lender);
				   if(errorList.size() > 0)
					   mapErrorList.put("First Date Of Payment", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getRepaymentFrequency()) || StringUtils.isNotBlank(customerRequest.getRepaymentFrequency())) {
				   List<String> errorList = customerFieldValidator.validateRepaymentFrequency(customerRequest.getRepaymentFrequency());
				   if(errorList.size() > 0)
					   mapErrorList.put("Repayment Frequency", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getTotalNoOfRepayment()) || (("NO".equalsIgnoreCase(customerRequestFields.getTotalNoOfRepayment())) && customerRequest.getTotalNoOfRepayment() != null && customerRequest.getTotalNoOfRepayment() >= 0)){
				   List<String> errorList = customerFieldValidator.validateTotalNoOfRepayment(customerRequest.getTotalNoOfRepayment());
				   if(errorList.size() > 0)
					   mapErrorList.put("Total Number Of Repayment", errorList);
			   }
			   if("YES".equalsIgnoreCase(customerRequestFields.getInstallmentAmount()) || (("NO".equalsIgnoreCase(customerRequestFields.getInstallmentAmount())) && customerRequest.getInstallmentAmount() != null && customerRequest.getInstallmentAmount() >= 0)) {
				   List<String> errorList = customerFieldValidator.validateInstallmentAmount(customerRequest.getInstallmentAmount());
				   if(errorList.size() > 0)
					   mapErrorList.put("Installment Amount", errorList);
			   }
			   
			   if(mapErrorList.size() > 0) {
				   ObjectMapper objectMapper = new ObjectMapper();
				   String json = "";
			        try {
			            json = objectMapper.writeValueAsString(mapErrorList);
			            log.error("Invalid data in customer request - " + json);
			        } catch (JsonProcessingException e) {
			        	throw new GeneralCustomException(ERROR, "Invalid data in customer request - " + mapErrorList);
			        }
				   throw new GeneralCustomException(ERROR, "Invalid data in customer request - " + json);
			   }
			   
		   } else {
			   log.error("No data available for given lender in the required fields table");
			   throw new GeneralCustomException(ERROR, "No data available for given lender in the required fields table");
		   }
	   } catch(GeneralCustomException e) {
		   throw e;
	   } catch(Exception e) {
		   log.error("Exception occured while validating the customer capture request");
		   throw e;
	   }
   }
   
   public RequestIdDetails validateRequestId(String requestId, String identifyProviderServiceUri, RestTemplate restTemplate) {
	   RequestIdDetails requestIdDtls = null;
	   try {
		   RequestIdResponseDTO requestIdResponseDTO = Optional.ofNullable(customerServiceHelper.fetchrequestIdDetails(requestId, identifyProviderServiceUri, restTemplate))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Request Id not found"));
			requestIdDtls = requestIdResponseDTO.getData();
			if(StringUtils.isNotBlank(requestIdDtls.getUserId())) {
				log.error("Customerservice createcustomer - Create customer failed as request id and customer id already exist in database.");
				throw new GeneralCustomException(ERROR ,"Create customer failed as request id and customer id already exist in database.");
		   }
	   }
	   catch(ServiceNotAvailableException e) {
		   log.error("Exception occured while fetching request Id details- Service unavailable");
		   throw new ServiceNotAvailableException(ERROR ,e.getMessage());
	   }
	   catch(Exception e) {
		   log.error("Exception occured while fetching request Id details");
		   throw new GeneralCustomException(ERROR ,e.getMessage());
	   }
	   return requestIdDtls;
   }
   
   public boolean addCustomerRequiredFields(CustomerRequestFields customerRequestFields) {
	   boolean isSuccess = false;
	   try {
		   validateCustomerRequestFields(customerRequestFields);
		   Optional<CustomerRequestFields> optCustomerRequestFields =  customerRequestFieldsRepository.findByLender(customerRequestFields.getLender());
		   if(optCustomerRequestFields.isPresent()) {
			   CustomerRequestFields customerRequestFieldsResp = optCustomerRequestFields.get();
			   customerRequestFieldsRepository.deleteById(customerRequestFieldsResp.getId());
		   }
		   CustomerRequestFields customerRequestFieldsResponse = customerRequestFieldsRepository.save(customerRequestFields);
		   if(customerRequestFieldsResponse != null)
			   isSuccess = true;
		   
	   }catch(GeneralCustomException e) {
		   log.error("Customerservice addCustomerRequiredFields - " + e.getMessage());
		   throw new GeneralCustomException(ERROR, e.getMessage());
	   }
	   catch(Exception e) {
		   log.error("Customerservice addCustomerRequiredFields - addCustomerRequiredFields failed.");
			throw new GeneralCustomException(ERROR ,"Customerservice addCustomerRequiredFields - addCustomerRequiredFields failed.");
	   }
	   return isSuccess;
   }
   
   public void validateCustomerRequestFields(CustomerRequestFields customerRequestFields) {
	   List<String> errorList = new ArrayList<String>();
	   Map<String, List<String>> mapErrorList =  new HashMap<String, List<String>>();
	   try {
		   if(customerRequestFields != null) {
			   if(StringUtils.isNotBlank(customerRequestFields.getFirstName()) && customerRequestFields.getFirstName().equalsIgnoreCase("NO")) {
				   errorList.add(AppConstants.FIRST_NAME_MANDATORY_MESSAGE);
				   mapErrorList.put("First Name", errorList);
			   }
			   if(StringUtils.isNotBlank(customerRequestFields.getLastName()) && customerRequestFields.getLastName().equalsIgnoreCase("NO")) {
				   errorList.add(AppConstants.LAST_NAME_MANDATORY_MESSAGE);
				   mapErrorList.put("Last Name", errorList);
			   }
			   if(StringUtils.isNotBlank(customerRequestFields.getMobileNo()) && customerRequestFields.getMobileNo().equalsIgnoreCase("NO")) {
				   errorList.add(AppConstants.MOBILENO_MANDATORY_MESSAGE);
				   mapErrorList.put("Mobile Number", errorList);
			   }
			   if(StringUtils.isNotBlank(customerRequestFields.getEmailId()) && customerRequestFields.getEmailId().equalsIgnoreCase("NO")) {
				   errorList.add(AppConstants.EMAIL_MANDATORY_MESSAGE);
				   mapErrorList.put("Email", errorList);
			   }
			   if(StringUtils.isNotBlank(customerRequestFields.getCallbackURLs()) && customerRequestFields.getCallbackURLs().equalsIgnoreCase("NO")) {
				   errorList.add(AppConstants.CALLBACKS_MANDATORY_MESSAGE);
				   mapErrorList.put("Callback URL", errorList);
			   }
			   
			   if(mapErrorList.size() > 0) {
				   ObjectMapper objectMapper = new ObjectMapper();
				   String json = "";
			        try {
			            json = objectMapper.writeValueAsString(mapErrorList);
			            log.error("Mandatory fields can't be made optional - " + json);
			        } catch (JsonProcessingException e) {
			        	throw new GeneralCustomException(ERROR, "Mandatory fields can't be made optional  - " + mapErrorList);
			        }
				   throw new GeneralCustomException(ERROR, "Mandatory fields can't be made optional  - " + json);
			   }
		   }
	   }catch(GeneralCustomException e) {
		   log.error("Mandatory fields can't be made optional - " + e.getMessage());
		   throw new GeneralCustomException(ERROR, e.getMessage());
	   }
	   catch(Exception e) {
		   log.error("Mandatory fields can't be made optional - " + e.getMessage());
		   throw new GeneralCustomException(ERROR, e.getMessage());
	   }
   }

    private void checkAndSavePayAllocation(RequestIdDetails requestIdDetails, CreateCustomerRequest customer) {
        String requestId = requestIdDetails.getRequestId();
        log.info(" Inside check And SavePayAllocation : Request ID : {} ",requestId);
        try {
            StateControllerInfo stateControllerInfo = linkServiceUtil.getStateInfo(requestId, requestIdDetails.getClientName());
            log.info(" response from stateControllerInfo {} : Request id : {} ",stateControllerInfo,requestId);
            boolean allocationStatus = linkServiceUtil.checkStateInfo(stateControllerInfo);
            if (allocationStatus) {
                OfferPayAllocationRequest offerPayAllocationRequest = linkServiceUtil.prepareCheckAffordabilityRequest(customer);
                OfferPayAllocationResponse offerPayAllocationResponse = linkServiceUtil.postCheckAffordabilityRequest(offerPayAllocationRequest, requestId);
                log.info(" offerPayAllocationResponse : {} : requestId {} ", offerPayAllocationResponse, requestId);
            }
        } catch (Exception ex) {
            log.error(" Error while doing checkAndSavePayAllocation {}  : requestId {} ", ex.getMessage(), requestId);
            throw new OfferPayAllocationException(" save allocation failed : " + ex.getMessage());
        }

    }

}

