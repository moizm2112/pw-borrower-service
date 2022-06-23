package com.paywallet.userservice.user.services;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.paywallet.userservice.user.dto.PayrollProviderDetailsDTO;
import com.paywallet.userservice.user.entities.*;
import com.paywallet.userservice.user.repository.CustomerDetailsRepository;
import com.paywallet.userservice.user.util.*;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paywallet.userservice.user.constant.AppConstants;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.enums.ProviderTypeEnum;
import com.paywallet.userservice.user.enums.StateStatus;
import com.paywallet.userservice.user.enums.VerificationStatusEnum;
import com.paywallet.userservice.user.exception.CreateCustomerABAException;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.CustomerAccountException;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.exception.FineractAPIException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.OfferPayAllocationException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.SMSAndEmailNotificationException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.AccountDetails;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.CustomerAccountResponseDTO;
import com.paywallet.userservice.user.model.CustomerRequestFields;
import com.paywallet.userservice.user.model.CustomerResponseDTO;
import com.paywallet.userservice.user.model.EmployerSearchDetailsDTO;
import com.paywallet.userservice.user.model.LenderConfigInfo;
import com.paywallet.userservice.user.model.LyonsAPIRequestDTO;
import com.paywallet.userservice.user.model.RequestIdDTO;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.RequestIdResponseDTO;
import com.paywallet.userservice.user.model.StateControllerInfo;
import com.paywallet.userservice.user.model.UpdateCustomerDetailsResponseDTO;
import com.paywallet.userservice.user.model.UpdateCustomerEmailIdDTO;
import com.paywallet.userservice.user.model.UpdateCustomerMobileNoDTO;
import com.paywallet.userservice.user.model.UpdateCustomerRequestDTO;
import com.paywallet.userservice.user.model.ValidateAccountRequest;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationRequestWrapperModel;
import com.paywallet.userservice.user.repository.CustomerRepository;
import com.paywallet.userservice.user.repository.CustomerRequestFieldsRepository;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@Component
@Service
@Slf4j
@RefreshScope
public class CustomerService {
	@Value("${customer.validation.maxAllowedUpdates}")
	private Integer maxAllowedUpdates;

	private static final String ERROR = "Error";
	private static final String RESULT = "result";
	private static final String STATUS_DESC = "statusDescription";
	private static final String VALID_RTN = "validRtn";
	private static final String ACCEPT = "Accept";
	private static final String DEPOSIT_ALLOCATION = "DEPOSIT_ALLOCATION";
	private static final String GENERAL = "GENERAL";
	private static final String EMPLOYMENT_VERIFICATION = "EMPLOYMENT_VERIFICATION";
	private static final String INCOME_VERIFICATION = "INCOME_VERIFICATION";
	private static final String IDENTITY_VERIFICATION = "IDENTITY_VERIFICATION";
	private static final String PDNOTSUPPORTED = "pd Not supported";


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
	RequestIdUtil requestIdUtil;

	@Autowired
	CustomerFieldValidator customerFieldValidator;

	@Autowired
	CustomerDetailsRepository customerProvidedDetailsRepository;

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
	 * This attribute holds the URI path of the Identity service provider
	 * Microservice
	 */
	@Value("${identifyProviderService.eureka.uri}")
	private String identifyProviderServiceUri;

	@Value("${fineract.clienttype}")
	private String fineractClientType;

	@Value("${createVirtualAccount.eureka.uri}")
	private String createVirtualAccountUri;

	@Autowired
	KafkaPublisherUtil kafkaPublisherUtil;

	@Autowired
	LinkServiceUtil linkServiceUtil;

	@Autowired
	CustomerWrapperAPIService customerWrapperAPIService;

	private LenderConfigInfo lenderConfigInfo;

	public static final String ROUTING_NUMBER = "284073808";
	private static final String EMAIL = "email";
	private static final String MOBILE = "mobile";

	@Autowired
	CommonUtil commonUtil;

	/**
	 * Method fetches customer details by cellPhone
	 * 
	 * @param customerId
	 * @return
	 * @throws CustomerNotFoundException
	 */
	public CustomerDetails getCustomer(String customerId) throws CustomerNotFoundException {
		log.debug("Inside getCustomer of CustomerService class" + customerId);
		Optional<CustomerDetails> optionalCustDetails = customerRepository.findByCustomerId(customerId);
		if (optionalCustDetails.isPresent()) {
			return optionalCustDetails.get();
		} else {
			throw new CustomerNotFoundException("Customer not present with the Submitted Details ");
		}
	}

	/**
	 * Method fetches customer details by cellPhone
	 * 
	 * @param cellPhone
	 * @return
	 * @throws CustomerNotFoundException
	 */
	public CustomerDetails getCustomerByMobileNo(String cellPhone) throws CustomerNotFoundException {
		log.debug("Inside getCustomer of CustomerService class" + cellPhone);
		if (!cellPhone.startsWith("+1") && cellPhone.length() == 10)
			cellPhone = "+1".concat(cellPhone);
		Optional<CustomerDetails> optionalCustDetails = customerRepository.findByPersonalProfileCellPhone(cellPhone);
		if (optionalCustDetails.isPresent()) {
			return optionalCustDetails.get();
		} else {
			throw new CustomerNotFoundException(
					"Customer not present with the cellPhone: " + cellPhone + " to fetch customer details");
		}
	}

	/**
	 * Methods gets customer account details by cellPhone.
	 * 
	 * @param cellPhone
	 * @return
	 * @throws CustomerAccountException
	 * @throws CustomerNotFoundException
	 */
	public AccountDetails getAccountDetails(String cellPhone)
			throws CustomerAccountException, CustomerNotFoundException {
		AccountDetails accountDetails = new AccountDetails();
		Optional<CustomerDetails> customerDetails = customerRepository.findByPersonalProfileCellPhone(cellPhone);
		if (customerDetails.isPresent()) {
			if (customerDetails.get().getSalaryProfile() != null) {
				if (CustomerServiceUtil.doesObjectContainField(customerDetails.get().getSalaryProfile(),
						"salaryAccount")) {
					/* sending only leading 4 digits of the accNo in response */
					if (customerDetails.get().getSalaryProfile().getSalaryAccount() != null) {
						Integer salaryAccNoLength = customerDetails.get().getSalaryProfile().getSalaryAccount()
								.length();
						String leadingFourDigitsOfSalAccNo = customerDetails.get().getSalaryProfile().getSalaryAccount()
								.substring(salaryAccNoLength - 4, salaryAccNoLength);
						accountDetails.setSalaryAccountNumber(leadingFourDigitsOfSalAccNo);
					} else {
						log.debug("Salary AccNo is NULL for the customer with cellPhone: " + cellPhone
								+ " in salary profile");
						throw new CustomerAccountException(
								"Salary AccNo is NULL for the customer with cellPhone: " + cellPhone);
					}

				} else {
					log.debug("Salary AccNo field not present for the customer with cellPhone: " + cellPhone
							+ " in salary profile");
					throw new CustomerAccountException(
							"Salary AccNo not updated for the customer with cellPhone: " + cellPhone);
				}

				if (CustomerServiceUtil.doesObjectContainField(customerDetails.get().getSalaryProfile(), "aba")) {
					/* sending only leading 4 digits of the abaOfAccNo in response */
					if (customerDetails.get().getSalaryProfile().getAba() != null) {
						Integer abaOfsalaryAccNoLength = customerDetails.get().getSalaryProfile().getAba().length();
						String leadingFourDigitsOfabaSalAccNo = customerDetails.get().getSalaryProfile().getAba()
								.substring(abaOfsalaryAccNoLength - 4, abaOfsalaryAccNoLength);
						accountDetails.setAccountABANumber(leadingFourDigitsOfabaSalAccNo);
					} else {
						log.debug("ABA Of Salary AccNo is NULL for the customer with cellPhone: " + cellPhone
								+ " in salary profile");
						throw new CustomerAccountException(
								"ABA Of Salary AccNo is NULL for the customer with cellPhone: " + cellPhone);
					}

				} else {
					log.debug("ABA of Salary AccNo not updated for the customer with cellPhone: " + cellPhone
							+ " in salary profile");
					throw new CustomerAccountException(
							"ABA of Salary AccNo not updated for the customer with cellPhone: " + cellPhone);
				}

			} else {
				log.debug("Salary details are not updated for the customer with cellPhone: " + cellPhone);
				throw new CustomerAccountException(
						"Salary details are not updated for the customer with cellPhone: " + cellPhone);
			}

		} else {
			log.debug("Customer not present with the cellPhone: " + cellPhone + " to fetch account details");
			throw new CustomerNotFoundException(
					"Customer not present with the cellPhone: " + cellPhone + " to fetch account details");
		}

		return accountDetails;
	}

	/**
	 * Method validates the customer account information against the lyons API
	 * 
	 * @param validateAccountRequest
	 * @return
	 * @throws CustomerNotFoundException
	 * @throws GeneralCustomException
	 */
	public CustomerDetails validateAccountRequest(ValidateAccountRequest validateAccountRequest)
			throws CustomerNotFoundException, GeneralCustomException {
		log.info("Inside of validateAccountRequest method");
		CustomerDetails updatedCustomer;
		boolean accntAndabaVerification = false;
		boolean incrementCounter = false;
		Optional<CustomerDetails> customerDetails = customerRepository
				.findByPersonalProfileCellPhone(validateAccountRequest.getCellPhone());
		if (customerDetails.isPresent()) {
			if (customerDetails.get().getUpdateCounter().equals(maxAllowedUpdates)) {
				log.error("Customer validation update attempts reached maximum allowed");
				throw new GeneralCustomException(ERROR, "Customer validation update attempts reached maximum allowed");
			} else {
				if (customerDetails.get().getSalaryProfile().getProvider()
						.equalsIgnoreCase(ProviderTypeEnum.ARGYLE.toString())) {
					String leadingFourDigitsOfSalAccNo = validateAccountRequest.getSalaryAccountNumber().substring(
							validateAccountRequest.getSalaryAccountNumber().length() - 4,
							validateAccountRequest.getSalaryAccountNumber().length());
					String leadingFourDigitsOfabaNo = validateAccountRequest.getAccountABANumber().substring(
							validateAccountRequest.getAccountABANumber().length() - 4,
							validateAccountRequest.getAccountABANumber().length());
					if (leadingFourDigitsOfSalAccNo
							.equalsIgnoreCase(customerDetails.get().getSalaryProfile().getSalaryAccount())
							&& leadingFourDigitsOfabaNo
									.equalsIgnoreCase(customerDetails.get().getSalaryProfile().getAba())) {
						log.info("provided customer account details are validated with the existing data from the DB");
						accntAndabaVerification = true;
					} else {
						log.warn(
								"provided customer account details ,either Salary AccNo or abaOfSalaryAccNo or not matching with existing details");
						incrementCounter = true;
					}

				}
				if (customerDetails.get().getSalaryProfile().getProvider()
						.equalsIgnoreCase(ProviderTypeEnum.ATOMICFI.toString())) {
					if (validateAccountRequest.getSalaryAccountNumber()
							.equalsIgnoreCase(customerDetails.get().getSalaryProfile().getSalaryAccount())
							&& validateAccountRequest.getAccountABANumber()
									.equalsIgnoreCase(customerDetails.get().getSalaryProfile().getAba())) {
						log.info("provided customer account details are validated with the existing data from the DB");
						accntAndabaVerification = true;
					} else {
						log.warn(
								"provided customer account details ,either Salary AccNo or abaOfSalaryAccNo or not matching with existing details");
						incrementCounter = true;
					}
				}
				if (customerDetails.get().getStatus().isEmpty()
						|| !customerDetails.get().getStatus().equalsIgnoreCase(ACCEPT)) {
					log.info("Lyons Call to validate Account details");
					JSONObject jsonObject = lyonsService.checkAccountOwnership(LyonsAPIRequestDTO.builder()
							.firstName(customerDetails.get().getPersonalProfile().getFirstName())
							.lastName(customerDetails.get().getPersonalProfile().getLastName())
							.accountNumber(validateAccountRequest.getSalaryAccountNumber())
							.abaNumber(validateAccountRequest.getAccountABANumber()).build());
					log.debug("Lyons Call result: " + jsonObject.toString());
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
					log.info(
							"As account details are validated successfully updating the DB with full SalaryAccNo & ABANo and resetting the counter to 0");
					customerDetails.get().setSalaryAccountNumber(validateAccountRequest.getSalaryAccountNumber());
					customerDetails.get().setAccountABANumber(validateAccountRequest.getAccountABANumber());
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
			throw new CustomerNotFoundException("Customer does not exists with the cellPhone: "
					+ validateAccountRequest.getCellPhone() + " to validate");
		}
	}

	/**
	 * Method updates the customer salary profile
	 * 
	 * @param updateCustomerRequest
	 * @return
	 * @throws CustomerNotFoundException
	 */
	public CustomerDetails updateCustomerDetails(UpdateCustomerRequestDTO updateCustomerRequest)
			throws CustomerNotFoundException {

		Optional<CustomerDetails> customerDetailsByMobileNo = customerRepository
				.findByPersonalProfileCellPhone(updateCustomerRequest.getCellPhone());

		if (customerDetailsByMobileNo.isPresent()) {
			log.debug("Customer with the cellphone no "
					+ customerDetailsByMobileNo.get().getPersonalProfile().getCellPhone() + " already exists");
			log.info("Customer details are getting updated...");

			if (updateCustomerRequest.getSalaryProfile() != null)
				customerDetailsByMobileNo.get().setSalaryProfile(updateCustomerRequest.getSalaryProfile());
			log.info("Customer details are updated successfully");
			return customerRepository.save(customerDetailsByMobileNo.get());
		} else {
			log.error("Customer does not exists with the cellPhone: " + updateCustomerRequest.getCellPhone()
					+ " to update");
			throw new CustomerNotFoundException("Customer does not exists with the cellPhone: "
					+ updateCustomerRequest.getCellPhone() + " to update");
		}
	}

	/**
	 * Method updates the customer Basic Details
	 * 
	 * @param updateCustomerMobileNoDTO
	 * @return
	 * @throws CustomerNotFoundException
	 */
	public UpdateCustomerDetailsResponseDTO updateCustomerMobileNo(UpdateCustomerMobileNoDTO updateCustomerMobileNoDTO,
			String requestId) throws CustomerNotFoundException, RequestIdNotFoundException {
		CustomerDetails custDetails = new CustomerDetails();
		boolean isMobileNoUpdatedInFineract = false;
		boolean isMobileNoUpdatedInCustomerDetails = false;
		UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponseDTO = new UpdateCustomerDetailsResponseDTO();
		try {

			if (!updateCustomerMobileNoDTO.getCellPhone().startsWith("+1")
					&& updateCustomerMobileNoDTO.getCellPhone().length() == 10) {
				updateCustomerMobileNoDTO.setCellPhone("+1".concat(updateCustomerMobileNoDTO.getCellPhone()));
			}
			if (!updateCustomerMobileNoDTO.getNewCellPhone().startsWith("+1")
					&& updateCustomerMobileNoDTO.getNewCellPhone().length() == 10) {
				updateCustomerMobileNoDTO.setNewCellPhone("+1".concat(updateCustomerMobileNoDTO.getNewCellPhone()));
			}
			if (updateCustomerMobileNoDTO.getCellPhone()
					.equalsIgnoreCase(updateCustomerMobileNoDTO.getNewCellPhone())) {
				log.error(
						"Please provide different CellPhone Number. You cannot enter CellPhone Number matching the customer data");
				throw new CustomerNotFoundException(
						"Please provide different CellPhone Number. You cannot enter CellPhone Number matching the customer data");
			}

			RequestIdResponseDTO requestIdResponseDTO = Optional.ofNullable(
					customerServiceHelper.fetchrequestIdDetails(requestId, identifyProviderServiceUri, restTemplate))
					.orElseThrow(() -> new RequestIdNotFoundException("Request Id not found"));
			RequestIdDetails requestIdDtls = requestIdResponseDTO.getData();
			if (StringUtils.isBlank(requestIdDtls.getUserId())) {
				throw new CustomerNotFoundException(
						"RequestId and cellPhone does not match. Please provide a valid requestId or cellPhone to update");
			}
			if (checkIfCredentialsVerified(requestIdDtls.getUserId(), MOBILE)) {
				throw new GeneralCustomException(ERROR, "Cell phone number already verified, cannot be updated");
			}
			if (StringUtils.isAllBlank(requestIdDtls.getAllocationStatus())) {
//	        	if( ((String) requestIdDtls.getEmployer()).equalsIgnoreCase(updateCustomerMobileNoDTO.getEmployerName())) {
				Optional<CustomerDetails> customerDetailsByMobileNo = customerRepository
						.findByPersonalProfileCellPhone(updateCustomerMobileNoDTO.getCellPhone());
				if (customerDetailsByMobileNo.isPresent()) {
					custDetails = customerDetailsByMobileNo.get();
					if (requestIdDtls.getUserId().equalsIgnoreCase(custDetails.getCustomerId())) {
						Optional<CustomerDetails> checkForMobileNumberinDB = customerRepository
								.findByPersonalProfileCellPhone(updateCustomerMobileNoDTO.getNewCellPhone());
						if (!checkForMobileNumberinDB.isPresent()) {

							log.debug("Customer with the cellphone no "
									+ custDetails.getPersonalProfile().getCellPhone() + " already exists");
							log.info("Customer details are getting updated...");

							if (!custDetails.getPersonalProfile().getCellPhone()
									.equalsIgnoreCase(updateCustomerMobileNoDTO.getCellPhone())) {
								log.error("Customer does nor exist for the given cellPhone number");
								throw new CustomerNotFoundException(
										"Provided CellPhone Number does not match the customer data. Please provide a valid CellPhone Number.");
							}

							if (custDetails.getPersonalProfile().getCellPhone()
									.equalsIgnoreCase(updateCustomerMobileNoDTO.getNewCellPhone())) {
								log.error(
										"CellPhone Number to be updated should be different from existing CellPhone Number");
								throw new CustomerNotFoundException(
										"CellPhone Number to be updated (" + updateCustomerMobileNoDTO.getNewCellPhone()
												+ ") should be different from existing CellPhone Number");
							}

							// Make an fineract call to update the external Id and cellPhone.
							if (StringUtils.isNotBlank(custDetails.getVirtualClientId())) {
								customerServiceHelper.updateMobileNoInFineract(
										updateCustomerMobileNoDTO.getNewCellPhone(), custDetails.getVirtualClientId());
								isMobileNoUpdatedInFineract = true;
							}
							// Update the Customer table
							custDetails.getPersonalProfile().setCellPhone(updateCustomerMobileNoDTO.getNewCellPhone());
							custDetails.setRequestId(requestId);

							updateCustomerDetailsResponseDTO.setRequestId(requestId);
							updateCustomerDetailsResponseDTO.setCellPhone(updateCustomerMobileNoDTO.getNewCellPhone());
							updateCustomerDetailsResponseDTO.setCustomerId(custDetails.getCustomerId());

							log.info("Customer CellPhone Number updated successfully");
							custDetails = customerRepository.save(custDetails);
							isMobileNoUpdatedInCustomerDetails = true;
						} else {
							log.error("CellPhone Number to be updated " + updateCustomerMobileNoDTO.getNewCellPhone()
									+ " exists in database");
							throw new CustomerNotFoundException("CellPhone Number to be updated "
									+ updateCustomerMobileNoDTO.getNewCellPhone() + " exists in database");
						}
					} else {
						log.error(
								"RequestId and cellPhone does not match. Please provide a valid requestId or cellPhone to update");
						throw new CustomerNotFoundException(
								"RequestId and cellPhone does not match. Please provide a valid requestId or cellPhone to update");
					}
				} else {
					log.error("Customer does not exists with the CellPhone Number: "
							+ updateCustomerMobileNoDTO.getCellPhone());
					throw new CustomerNotFoundException("Customer does not exists with the CellPhone Number: "
							+ updateCustomerMobileNoDTO.getCellPhone());
				}
//	        	}
//	        	else {
//	        		log.error("Employer name doesn't match with the existing customer details");
//	                throw new GeneralCustomException(ERROR, "Employer name doesn't match with the existing customer details "+updateCustomerMobileNoDTO.getEmployerName()+" to update");
//	        	}
			} else {
				log.error("CellPhone Number cannot be updated as allocation has already been completed");
				throw new CustomerNotFoundException(
						"CellPhone Number cannot be updated as allocation has already been completed");
			}
		} catch (FineractAPIException e) {
			Sentry.captureException(e);
			log.error(
					"Exception occured in fineract while updating CellPhone Number for given client " + e.getMessage());
			throw new FineractAPIException(e.getMessage());
		} catch (CustomerNotFoundException e) {
			Sentry.captureException(e);
			if (isMobileNoUpdatedInFineract && !isMobileNoUpdatedInCustomerDetails)
				customerServiceHelper.updateMobileNoInFineract(updateCustomerMobileNoDTO.getCellPhone(),
						custDetails.getVirtualClientId());
			log.error("Exception occured while updating customer details " + e.getMessage());
			throw new CustomerNotFoundException(e.getMessage());
		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			if (isMobileNoUpdatedInFineract && !isMobileNoUpdatedInCustomerDetails)
				customerServiceHelper.updateMobileNoInFineract(updateCustomerMobileNoDTO.getCellPhone(),
						custDetails.getVirtualClientId());
			log.error("Exception occured while updating customer details " + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		} catch (Exception e) {
			Sentry.captureException(e);
			if (isMobileNoUpdatedInFineract && !isMobileNoUpdatedInCustomerDetails)
				customerServiceHelper.updateMobileNoInFineract(updateCustomerMobileNoDTO.getCellPhone(),
						custDetails.getVirtualClientId());
			log.error("Exception occured while updating customer details " + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		}
		return updateCustomerDetailsResponseDTO;
	}

	/**
	 * Method updates the customer Basic Details
	 * 
	 * @param updateCustomerEmailIdDTO
	 * @return
	 * @throws CustomerNotFoundException
	 */
	public UpdateCustomerDetailsResponseDTO updateCustomerEmailId(UpdateCustomerEmailIdDTO updateCustomerEmailIdDTO,
			String requestId) throws CustomerNotFoundException, RequestIdNotFoundException {
		CustomerDetails custDetails = new CustomerDetails();
		UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponseDTO = new UpdateCustomerDetailsResponseDTO();
		try {

			if (!updateCustomerEmailIdDTO.getCellPhone().startsWith("+1")
					&& updateCustomerEmailIdDTO.getCellPhone().length() == 10)
				updateCustomerEmailIdDTO.setCellPhone("+1".concat(updateCustomerEmailIdDTO.getCellPhone()));

			if (updateCustomerEmailIdDTO.getEmailId().equalsIgnoreCase(updateCustomerEmailIdDTO.getNewEmailId())) {
				log.error("You cannot enter exactly same emailId to update");
				throw new CustomerNotFoundException("You cannot enter exactly same emailId to update");
			}

			RequestIdResponseDTO requestIdResponseDTO = Optional.ofNullable(
					customerServiceHelper.fetchrequestIdDetails(requestId, identifyProviderServiceUri, restTemplate))
					.orElseThrow(() -> new RequestIdNotFoundException("Request Id not found"));
			RequestIdDetails requestIdDtls = requestIdResponseDTO.getData();
			if (StringUtils.isBlank(requestIdDtls.getUserId())) {
				throw new CustomerNotFoundException(
						"RequestId and cellPhone does not match. Please provide a valid requestId or cellPhone to update");
			}

			if (checkIfCredentialsVerified(requestIdDtls.getUserId(), EMAIL)) {
				throw new GeneralCustomException(ERROR, "Email Id already verified, cannot be updated");
			}
			if (StringUtils.isAllBlank(requestIdDtls.getAllocationStatus())) {
//        		if (((String) requestIdDtls.getEmployer()).equalsIgnoreCase(updateCustomerEmailIdDTO.getEmployerName())) {
				Optional<CustomerDetails> customerDetailsByMobileNo = customerRepository
						.findByPersonalProfileCellPhone(updateCustomerEmailIdDTO.getCellPhone());
				if (customerDetailsByMobileNo.isPresent()) {
					custDetails = customerDetailsByMobileNo.get();
					if (requestIdDtls.getUserId().equalsIgnoreCase(custDetails.getCustomerId())) {
						if (custDetails.getPersonalProfile().getEmailId()
								.equalsIgnoreCase(updateCustomerEmailIdDTO.getEmailId())) {
							Optional<CustomerDetails> checkForEmailIdInDB = customerRepository
									.findByPersonalProfileEmailId(updateCustomerEmailIdDTO.getNewEmailId());
							if (!checkForEmailIdInDB.isPresent()) {

								log.debug("Customer with the cellphone no "
										+ custDetails.getPersonalProfile().getCellPhone() + " already exists");
								log.info("Customer details are getting updated...");

								if (!custDetails.getPersonalProfile().getEmailId()
										.equalsIgnoreCase(updateCustomerEmailIdDTO.getEmailId())) {
									log.error("Customer does not exist for the given emailId");
									throw new CustomerNotFoundException(
											"Customer does not exist for the provided emailId ("
													+ updateCustomerEmailIdDTO.getEmailId() + ")");
								}

								if (custDetails.getPersonalProfile().getEmailId()
										.equalsIgnoreCase(updateCustomerEmailIdDTO.getNewEmailId())) {
									log.error("Updating EmailId should be different from existing emailId");
									throw new CustomerNotFoundException(
											"EmailId (" + updateCustomerEmailIdDTO.getNewEmailId()
													+ ") should be different from existing emailId");
								}

								if (custDetails.getPersonalProfile().getEmailId()
										.equalsIgnoreCase(updateCustomerEmailIdDTO.getEmailId())) {
									custDetails.getPersonalProfile()
											.setEmailId(updateCustomerEmailIdDTO.getNewEmailId());
									log.info("Customer Email Id updated successfully");
									custDetails = customerRepository.save(custDetails);
									custDetails.setRequestId(requestId);
									updateCustomerDetailsResponseDTO.setRequestId(requestId);
									updateCustomerDetailsResponseDTO
											.setCellPhone(updateCustomerEmailIdDTO.getCellPhone());
									updateCustomerDetailsResponseDTO
											.setEmailId(updateCustomerEmailIdDTO.getNewEmailId());
									updateCustomerDetailsResponseDTO.setCustomerId(custDetails.getCustomerId());
								} else {
									log.error("EmailId do not match with the existing customer details");
									throw new CustomerNotFoundException(
											"EmailId (" + updateCustomerEmailIdDTO.getEmailId()
													+ ") do not match with the existing customer details");
								}
							} else {
								log.error("EmailId " + updateCustomerEmailIdDTO.getNewEmailId()
										+ " exists in database. Please provide different email");
								throw new CustomerNotFoundException(
										"EmailId " + updateCustomerEmailIdDTO.getNewEmailId()
												+ " exists in database. Please provide different email");
							}
						} else {
							log.error("Provided email doesn't match with the customer's email");
							throw new CustomerNotFoundException(
									"\"Provided email " + updateCustomerEmailIdDTO.getEmailId()
											+ " doesn't match with the customer's email.");
						}
					} else {
						log.error(
								"RequestId and cellPhone does not match. Please provide a valid requestId or cellPhone to update");
						throw new CustomerNotFoundException(
								"RequestId and cellPhone does not match. Please provide a valid requestId or cellPhone to update");
					}
				} else {
					log.error("Customer does not exists with the cellPhone number: "
							+ updateCustomerEmailIdDTO.getCellPhone());
					throw new CustomerNotFoundException("Customer does not exists with the cellPhone number: "
							+ updateCustomerEmailIdDTO.getCellPhone());
				}
//        		}
//    			else {
//    				log.error("mployer Name do not match with the existing customer details");
//                    throw new CustomerNotFoundException("Employer Name (" + updateCustomerEmailIdDTO.getEmployerName() + ") do not match with the existing customer details");
//    			}

			} else {
				log.error("Email Id cannot be updated as allocation has already been completed");
				throw new CustomerNotFoundException(
						"Email Id cannot be updated as allocation has already been completed");
			}
		} catch (CustomerNotFoundException e) {
			Sentry.captureException(e);
			log.error("Exception occured while updating emailId customer details " + e.getMessage());
			throw new CustomerNotFoundException(e.getMessage());
		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			log.error("Exception occured while updating emailIdcustomer details " + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		} catch (Exception e) {
			Sentry.captureException(e);
			if (e.getMessage().contains("returned non unique result")) {
				log.error("Email Id " + updateCustomerEmailIdDTO.getNewEmailId()
						+ " exists in database. Please provide different email");
				throw new CustomerNotFoundException("Email Id " + updateCustomerEmailIdDTO.getNewEmailId()
						+ " exists in database. Please provide different email");
			} else {
				log.error("Exception occured while updating emailId customer details " + e.getMessage());
				throw new GeneralCustomException(ERROR, e.getMessage());
			}
		}
		return updateCustomerDetailsResponseDTO;
	}

	/**
	 * Method creates a response DTO to orchestrate back to the caller. This shares
	 * the response of customer details, status of request and URI path.
	 * 
	 * @param customerDetails
	 * @param message
	 * @param status
	 * @param path
	 * @return
	 */
	public CustomerResponseDTO prepareResponseDTO(CustomerDetails customerDetails, String message, int status,
			String path) {
		return CustomerResponseDTO.builder().data(customerDetails).message(message).status(status).timeStamp(new Date())
				.path(path).requestId(customerDetails.getRequestId()).build();
	}

	/**
	 * Method creates a response DTO to orchestrate back to the caller. This shares
	 * the response of customer details, status of request and URI path.
	 * 
	 * @param updateCustomerDetailsResponseDTO
	 * @param message
	 * @param status
	 * @param path
	 * @return
	 */
	public ResponseEntity<Object> prepareUpdateResponse(
			UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponseDTO, String message, int status,
			String path) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("data", updateCustomerDetailsResponseDTO);
		body.put("message", message);
		body.put("status", status);
		body.put("timestamp", new Date());
		body.put("path", path);
		return new ResponseEntity<>(body, HttpStatus.OK);
	}

	/**
	 * Method creates a response DTO to orchestrate back to the caller. This shares
	 * the response of customer details, status of request and URI path.
	 * 
	 * @param customerDetails
	 * @param message
	 * @param status
	 * @param path
	 * @return
	 */
	public ResponseEntity<Object> prepareResponse(CustomerDetails customerDetails, String message, int status,
			String path) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("data", customerDetails);
		body.put("message", message);
		body.put("status", status);
		body.put("timestamp", new Date());
		body.put("path", path);
		body.put("requestId", customerDetails.getRequestId());
//        if(customerDetails.isEmailNotificationSuccess())
//        	body.put("Email Notification", EMAIL_NOTIFICATION_SUCCESS);
//        else
//        	body.put("Email Notification", customerDetails.getPersonalProfile().getEmailId() + " - " + EMAIL_NOTIFICATION_FAILED);
//        if(customerDetails.isSmsNotificationSuccess())
//        	body.put("SMS Notification", SMS_NOTIFICATION_SUCCESS);
//        else
//        	body.put("SMS Notification", customerDetails.getPersonalProfile().getCellPhone() + " - " + SMS_NOTIFICATION_FAILED);

		if (status == 201) {
			return new ResponseEntity<>(body, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(body, HttpStatus.OK);
	}

	/**
	 * Method creates a response DTO to orchestrate back to the caller. This shares
	 * the response of customer Account details, status of request and URI path.
	 * 
	 * @param accountDetails
	 * @param message
	 * @param status
	 * @param path
	 * @return
	 */
	public CustomerAccountResponseDTO prepareAccountDetailsResponseDTO(AccountDetails accountDetails, String message,
			int status, String path) {
		return CustomerAccountResponseDTO.builder().data(accountDetails).message(message).status(status)
				.timeStamp(new Date()).path(path).build();
	}

	public void validateCreateCustomerRequest(CreateCustomerRequest customerRequest, String requestId, String lender) {
		Map<String, List<String>> mapErrorList = new HashMap<String, List<String>>();
		try {
			Optional<CustomerRequestFields> optionalCustomerRequestFields = customerRequestFieldsRepository
					.findByLender(lender);
			if (optionalCustomerRequestFields.isPresent()) {
				CustomerRequestFields customerRequestFields = Optional.ofNullable(optionalCustomerRequestFields.get())
						.orElseThrow(() -> new GeneralCustomException(ERROR,
								"Exception occured while fetching required fields for employer"));

				if ("YES".equalsIgnoreCase(customerRequestFields.getFirstName())
						|| StringUtils.isNotBlank(customerRequest.getFirstName())) {
					List<String> errorList = customerFieldValidator.validateFirstName(customerRequest.getFirstName());
					if (errorList.size() > 0)
						mapErrorList.put("First Name", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getLastName())
						|| StringUtils.isNotBlank(customerRequest.getLastName())) {
					List<String> errorList = customerFieldValidator.validateLastName(customerRequest.getLastName());
					if (errorList.size() > 0)
						mapErrorList.put("Last Name", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getCellPhone())
						|| StringUtils.isNotBlank(customerRequest.getCellPhone())) {
					List<String> errorList = customerFieldValidator.validateMobileNo(customerRequest.getCellPhone());
					if (errorList.size() > 0)
						mapErrorList.put("CellPhone Number", errorList);
				}

				if ("YES".equalsIgnoreCase(customerRequestFields.getMiddleName())
						|| StringUtils.isNotBlank(customerRequest.getMiddleName())) {
					List<String> errorList = customerFieldValidator.validateMiddleName(customerRequest.getMiddleName());
					if (errorList.size() > 0)
						mapErrorList.put("Middle Name", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getAddressLine1())
						|| StringUtils.isNotBlank(customerRequest.getAddressLine1())) {
					List<String> errorList = customerFieldValidator
							.validateAddressLine1(customerRequest.getAddressLine1());
					if (errorList.size() > 0)
						mapErrorList.put("Address Line1", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getAddressLine2())
						|| StringUtils.isNotBlank(customerRequest.getAddressLine2())) {
					List<String> errorList = customerFieldValidator
							.validateAddressLine2(customerRequest.getAddressLine2());
					if (errorList.size() > 0)
						mapErrorList.put("Address Line2", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getCity())
						|| StringUtils.isNotBlank(customerRequest.getCity())) {
					List<String> errorList = customerFieldValidator.validateCity(customerRequest.getCity());
					if (errorList.size() > 0)
						mapErrorList.put("City", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getState())
						|| StringUtils.isNotBlank(customerRequest.getState())) {
					List<String> errorList = customerFieldValidator.validateState(customerRequest.getState());
					if (errorList.size() > 0)
						mapErrorList.put("State", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getZip())
						|| StringUtils.isNotEmpty(customerRequest.getZip())) {
					List<String> errorList = customerFieldValidator.validateZip(customerRequest.getZip());
					if (errorList.size() > 0)
						mapErrorList.put("Zip", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getLast4TIN())
						|| StringUtils.isNotBlank(customerRequest.getLast4TIN())) {
					List<String> errorList = customerFieldValidator.validateLast4TIN(customerRequest.getLast4TIN());
					if (errorList.size() > 0)
						mapErrorList.put("Last 4TIN", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getDateOfBirth())
						|| StringUtils.isNotBlank(customerRequest.getDateOfBirth())) {
					List<String> errorList = customerFieldValidator
							.validateDateOfBirth(customerRequest.getDateOfBirth());
					if (errorList.size() > 0)
						mapErrorList.put("Date Of Birth", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getEmailId())
						|| StringUtils.isNotBlank(customerRequest.getEmailId())) {
					List<String> errorList = customerFieldValidator.validateEmailId(customerRequest.getEmailId(),
							customerRepository, customerRequest.getCellPhone());
					if (errorList.size() > 0)
						mapErrorList.put("EmailId", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getCallbackURLs())
						|| customerRequest.getCallbackURLs() != null) {
					List<String> errorList = customerFieldValidator.validateCallbackURLs(
							customerRequest.getCallbackURLs(), restTemplate, requestId, lender, lenderConfigInfo);
					if (errorList.size() > 0)
						mapErrorList.put("Callback URLS", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getFirstDateOfPayment())
						|| StringUtils.isNotBlank(customerRequest.getFirstDateOfPayment())) {
					List<String> errorList = customerFieldValidator
							.validateFirstDateOfPayment(customerRequest.getFirstDateOfPayment(), lender);
					if (errorList.size() > 0)
						mapErrorList.put("First Date Of Payment", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getRepaymentFrequency())
						|| StringUtils.isNotBlank(customerRequest.getRepaymentFrequency())) {
					List<String> errorList = customerFieldValidator
							.validateRepaymentFrequency(customerRequest.getRepaymentFrequency());
					if (errorList.size() > 0)
						mapErrorList.put("Repayment Frequency", errorList);
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getNumberOfInstallments())) {
					List<String> errorList = customerFieldValidator
							.validateTotalNoOfRepayment(customerRequest.getNumberOfInstallments());
					if (errorList.size() > 0)
						mapErrorList.put("Number of installments", errorList);
				} else {
					if (lenderConfigInfo == null) {
						lenderConfigInfo = customerFieldValidator.fetchLenderConfigurationForCallBack(requestId,
								restTemplate, lender);
						lenderConfigInfo = Optional.ofNullable(lenderConfigInfo)
								.orElseThrow(() -> new GeneralCustomException("ERROR",
										"Error while fetching lender configuration for validation"));
					}
					if ("YES".equalsIgnoreCase(lenderConfigInfo.getInvokeAndPublishDepositAllocation().name())) {
						List<String> errorList = new ArrayList<String>();
						if (customerRequest.getNumberOfInstallments() == null
								|| customerRequest.getNumberOfInstallments() <= 0) {
							errorList.add(AppConstants.NUMBEROFINSTALLMENTS_MANDATORY_MESSAGE);
							mapErrorList.put("Number of installments", errorList);
						}
					} else if (customerRequest.getNumberOfInstallments() != null
							|| customerRequest.getNumberOfInstallments() >= 0) {
						List<String> errorList = customerFieldValidator
								.validateTotalNoOfRepayment(customerRequest.getNumberOfInstallments());
						if (errorList.size() > 0)
							mapErrorList.put("Number of installments", errorList);
					}
				}
				if ("YES".equalsIgnoreCase(customerRequestFields.getInstallmentAmount())) {
					List<String> errorList = customerFieldValidator
							.validateInstallmentAmount(customerRequest.getInstallmentAmount());
					if (errorList.size() > 0)
						mapErrorList.put("Installment Amount", errorList);
				} else {
					if (lenderConfigInfo == null) {
						lenderConfigInfo = customerFieldValidator.fetchLenderConfigurationForCallBack(requestId,
								restTemplate, lender);
						lenderConfigInfo = Optional.ofNullable(lenderConfigInfo)
								.orElseThrow(() -> new GeneralCustomException("ERROR",
										"Error while fetching lender configuration for validation"));
					}
					if ("YES".equalsIgnoreCase(lenderConfigInfo.getInvokeAndPublishDepositAllocation().name())) {
						List<String> errorList = new ArrayList<String>();
						if (customerRequest.getInstallmentAmount() == null
								|| customerRequest.getInstallmentAmount() <= 0) {
							errorList.add(AppConstants.INSTALLMENTAMOUNT_MANDATORY_MESSAGE);
							mapErrorList.put("Installment amount", errorList);
						}
					} else if (customerRequest.getInstallmentAmount() != null
							|| customerRequest.getInstallmentAmount() >= 0) {
						List<String> errorList = customerFieldValidator
								.validateInstallmentAmount(customerRequest.getInstallmentAmount());
						if (errorList.size() > 0)
							mapErrorList.put("Installment Amount", errorList);
					}
				}

				if (mapErrorList.size() > 0) {
					ObjectMapper objectMapper = new ObjectMapper();
					String json = "";
					try {
						json = objectMapper.writeValueAsString(mapErrorList);
						log.error("Invalid data in customer request - " + json);
					} catch (JsonProcessingException e) {
						Sentry.captureException(e);
						throw new GeneralCustomException(ERROR, "Invalid data in customer request - " + mapErrorList);
					}
					throw new GeneralCustomException(ERROR, "Invalid data in customer request - " + json);
				}

			} else {
				log.error("No data available for given lender in the required fields table");
				throw new GeneralCustomException(ERROR,
						"No data available for given lender in the required fields table");
			}
		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			throw e;
		} catch (Exception e) {
			Sentry.captureException(e);
			log.error("Exception occured while validating the customer capture request");
			throw e;
		}
	}

	public boolean addCustomerRequiredFields(CustomerRequestFields customerRequestFields) {
		boolean isSuccess = false;
		try {
			customerServiceHelper.validateCustomerRequestFields(customerRequestFields);
			Optional<CustomerRequestFields> optCustomerRequestFields = customerRequestFieldsRepository
					.findByLender(customerRequestFields.getLender());
			if (optCustomerRequestFields.isPresent()) {
				CustomerRequestFields customerRequestFieldsResp = optCustomerRequestFields.get();
				customerRequestFieldsRepository.deleteById(customerRequestFieldsResp.getId());
			}
			CustomerRequestFields customerRequestFieldsResponse = customerRequestFieldsRepository
					.save(customerRequestFields);
			if (customerRequestFieldsResponse != null)
				isSuccess = true;

		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			log.error("Customerservice addCustomerRequiredFields - " + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		} catch (Exception e) {
			Sentry.captureException(e);
			log.error("Customerservice addCustomerRequiredFields - addCustomerRequiredFields failed.");
			throw new GeneralCustomException(ERROR,
					"Customerservice addCustomerRequiredFields - addCustomerRequiredFields failed.");
		}
		return isSuccess;
	}

	private void checkAndSavePayAllocation(RequestIdDetails requestIdDetails, CreateCustomerRequest customer,
			FlowTypeEnum flowtype, int loanAmount) {
		String requestId = requestIdDetails.getRequestId();
		log.info(" Inside check And SavePayAllocation : Request ID : {} ", requestId);
		boolean isDepositAllocation = false;
		if (flowtype.name().equals(FlowTypeEnum.DEPOSIT_ALLOCATION.name()))
			isDepositAllocation = true;
		try {
			StateControllerInfo stateControllerInfo = linkServiceUtil.getStateInfo(requestId,
					requestIdDetails.getClientName());
			log.info(" response from stateControllerInfo {} : Request id : {} ", stateControllerInfo, requestId);
			boolean allocationStatus = linkServiceUtil.checkStateInfo(stateControllerInfo);
			if (allocationStatus || (isDepositAllocation
					&& (StateStatus.YES).equals(stateControllerInfo.getInvokeAndPublishDepositAllocation()))) {
				OfferPayAllocationRequest offerPayAllocationRequest = linkServiceUtil
						.prepareCheckAffordabilityRequest(customer, loanAmount);
				log.info(" offerPayAllocationRequest : {} : requestId {} ", offerPayAllocationRequest, requestId);
				OfferPayAllocationResponse offerPayAllocationResponse = linkServiceUtil
						.postCheckAffordabilityRequest(offerPayAllocationRequest, requestId);
				log.info(" offerPayAllocationResponse : {} : requestId {} ", offerPayAllocationResponse, requestId);
			}
		} catch (Exception ex) {
			Sentry.captureException(ex);
			log.error(" Error while doing checkAndSavePayAllocation {}  : requestId {} ", ex.getMessage(), requestId);
			throw new OfferPayAllocationException(" save allocation failed : " + ex.getMessage());
		}

	}

	public void generalCustomerRequestConfig(CreateCustomerRequest customer) {
		if (!customer.getCellPhone().startsWith("+1") && customer.getCellPhone().length() == 10)
			customer.setCellPhone("+1".concat(customer.getCellPhone()));

		// Setup made to get integer field in request and if null set it to default to
		// 0.
		if (customer.getNumberOfInstallments() == null)
			customer.setNumberOfInstallments(0);
		if (customer.getInstallmentAmount() == null)
			customer.setInstallmentAmount(0);
	}

	public <T> CustomerDetails createCustomer(CreateCustomerRequest customer, String requestId, T obj,
			FlowTypeEnum flowType) throws CreateCustomerException, GeneralCustomException, ServiceNotAvailableException,
			RequestIdNotFoundException, SMSAndEmailNotificationException {
		log.info("Inside createCustomer of CustomerService class");
		int virtualAccount = -1;
		CustomerDetails saveCustomer = new CustomerDetails();
		RequestIdDetails requestIdDtls = null;
		CustomerDetails customerEntity = new CustomerDetails();
		DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel = null;
		EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel = null;
		IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel = null;
		IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel = null;
		double directDepositAllocationInstallmentAmount = 0;
		boolean isFineractAccountCreatedForExistingCustomer = false;
		boolean isEmployerPdSupported = true;

		if (!flowType.name().equals(FlowTypeEnum.GENERAL.name())) {
			if (obj.getClass().getSimpleName().equals((DepositAllocationRequestWrapperModel.class).getSimpleName()))
				depositAllocationRequestWrapperModel = (DepositAllocationRequestWrapperModel) obj;
			else if (obj.getClass().getSimpleName()
					.equals((EmploymentVerificationRequestWrapperModel.class).getSimpleName()))
				employmentVerificationRequestWrapperModel = (EmploymentVerificationRequestWrapperModel) obj;
			else if (obj.getClass().getSimpleName()
					.equals((IncomeVerificationRequestWrapperModel.class).getSimpleName()))
				incomeVerificationRequestWrapperModel = (IncomeVerificationRequestWrapperModel) obj;
			else if (obj.getClass().getSimpleName()
					.equals((IdentityVerificationRequestWrapperModel.class).getSimpleName()))
				identityVerificationRequestWrapperModel = (IdentityVerificationRequestWrapperModel) obj;
		}
		generalCustomerRequestConfig(customer);
		try {
			requestIdDtls = customerServiceHelper.validateRequestId(requestId, identifyProviderServiceUri, restTemplate,
					customer, customerRepository);
			lenderConfigInfo = customerFieldValidator.fetchLenderConfigurationForCallBack(requestId, restTemplate,
					requestIdDtls.getClientName());
			lenderConfigInfo = Optional.ofNullable(lenderConfigInfo)
					.orElseThrow(() -> new GeneralCustomException("ERROR",
							"Error while fetching lender configuration for validating callback urls"));
			//PWMVP2-503 || CHECKING REQUEST IN PROGRESS
			Optional<FlowTypeEnum> optionalFlowTypeEnum = customerServiceHelper.fetchInProgressRequestFlow(requestIdDtls);
			if(optionalFlowTypeEnum.isPresent()){
				FlowTypeEnum flowTypeEnum = optionalFlowTypeEnum.get();
				log.warn("Request {} in progress for the requestId Please complete the request and re-try again",flowTypeEnum);
				throw new GeneralCustomException("ERROR",new StringBuilder("Request [").append(flowTypeEnum).append("] in progress for the requestId Please complete the request and re-try again").toString());
			}

			switch (flowType.name()) {
			case GENERAL: {
				validateCreateCustomerRequest(customer, requestId, requestIdDtls.getClientName());
				//Check if the employer PD supported
				isEmployerPdSupported = customerServiceHelper.checkIfEmployerPdSuported(requestIdDtls);
				 
				customerEntity = checkAndReturnIfCustomerAlreadyExist(customer, lenderConfigInfo, requestId);
				if (!customerEntity.isExistingCustomer()) {
					if ("YES".equalsIgnoreCase(lenderConfigInfo.getInvokeAndPublishDepositAllocation().name())) {
						customerEntity = customerServiceHelper
								.createFineractVirtualAccount(requestIdDtls.getRequestId(), customerEntity);
						customerEntity.setAccountABANumber(ROUTING_NUMBER);
						log.info("Virtual fineract account created successfully ");
					}
				} else {
					if (StringUtils.isBlank(customerEntity.getAccountABANumber())
							&& StringUtils.isNotBlank(customerEntity.getVirtualAccount())) {
						customerEntity.setAccountABANumber(ROUTING_NUMBER);
					}

					if (StringUtils.isBlank(customerEntity.getVirtualAccount())
							&& StringUtils.isBlank(customerEntity.getAccountABANumber())) {
						if ("YES".equalsIgnoreCase(lenderConfigInfo.getInvokeAndPublishDepositAllocation().name())) {
							customerEntity = customerServiceHelper
									.createFineractVirtualAccount(requestIdDtls.getRequestId(), customerEntity);
							customerEntity.setAccountABANumber(ROUTING_NUMBER);
							log.info("Virtual fineract account created successfully");
							isFineractAccountCreatedForExistingCustomer = true;
						}
					}
				}
				break;
			}
			case DEPOSIT_ALLOCATION: {
				/*
				 * Validating the request against lender configuration to check whether it is a
				 * valid deposit allocation request
				 */
				if (lenderConfigInfo.getInvokeAndPublishDepositAllocation().equals(StateStatus.NO)) {
					throw new GeneralCustomException("ERROR", "Deposit allocation is not allowed for the lender");
				}
				if (!depositAllocationRequestWrapperModel.getCellPhone().startsWith("+1")
						&& depositAllocationRequestWrapperModel.getCellPhone().length() == 10)
					depositAllocationRequestWrapperModel
							.setCellPhone("+1".concat(depositAllocationRequestWrapperModel.getCellPhone()));

				/*
				 * Check if employer selection is done, else make a search and select employer
				 * to update employer details to request table
				 */
				if (requestIdDtls.getEmployer() == null || requestIdDtls.getEmployerPWId() == null) {
					requestIdDtls = getEmployerDetailsBasedOnEmplyerIdFromRequest(
							depositAllocationRequestWrapperModel.getEmployerId(), requestId, requestIdDtls);
				}
				
				//Check if the employer PD supported
				isEmployerPdSupported = customerServiceHelper.checkIfEmployerPdSuported(requestIdDtls);

			
				//if employer is not pd supported then we can stop the flow here
//				if(!customerServiceHelper.checkIfEmployerPdSuported(requestIdDtls)) {
//					throw new GeneralCustomException(PDNOTSUPPORTED, "Pay distribution is not supported for the employer "
//							+ requestIdDtls.getEmployer());
//				}
				// Validation of direct deposit allocation request
				log.info("validation started******"+depositAllocationRequestWrapperModel.getExternalVirtualAccount()+"==="+depositAllocationRequestWrapperModel.getExternalVirtualAccountABANumber());
				if(StringUtils.isNotBlank(depositAllocationRequestWrapperModel.getExternalVirtualAccount())
						&& StringUtils.isNotBlank(
								depositAllocationRequestWrapperModel.getExternalVirtualAccountABANumber())) {
					Boolean checkABAandVirtualAccountNumber = checkABAandVirtualAccountNumber(depositAllocationRequestWrapperModel, requestId);
					if(checkABAandVirtualAccountNumber) {
						log.info("query resulted the data as true");
						 throw new CreateCustomerABAException("ExternalVirtualAccountABANumber and ExternalVirtualAccountNumber should not be same");
					}
				}
				customerWrapperAPIService.validateDepositAllocationRequest(depositAllocationRequestWrapperModel,
						requestId, requestIdDtls, lenderConfigInfo);
				if (depositAllocationRequestWrapperModel.getLoanAmount() > 0) {
					double installmentAmount = getInstallmentAmount(
							depositAllocationRequestWrapperModel.getLoanAmount(),
							depositAllocationRequestWrapperModel.getInstallmentAmount(),
							depositAllocationRequestWrapperModel.getNumberOfInstallments());
					directDepositAllocationInstallmentAmount = installmentAmount;
				}
				customerEntity = checkAndReturnIfCustomerAlreadyExist(customer, lenderConfigInfo, requestId);
				log.info("Customer entity : " + customerEntity);
				if (!customerEntity.isExistingCustomer()) {
					if (StringUtils.isNotBlank(depositAllocationRequestWrapperModel.getExternalVirtualAccount())
							&& StringUtils.isNotBlank(
									depositAllocationRequestWrapperModel.getExternalVirtualAccountABANumber())) {
						customerEntity.setExternalAccount(depositAllocationRequestWrapperModel.getExternalVirtualAccount());
						customerEntity.setExternalAccountABA(depositAllocationRequestWrapperModel.getExternalVirtualAccountABANumber());
					} else if (StringUtils.isBlank(depositAllocationRequestWrapperModel.getExternalVirtualAccount())
							|| StringUtils.isBlank(
									depositAllocationRequestWrapperModel.getExternalVirtualAccountABANumber())) {
						customerEntity = customerServiceHelper
								.createFineractVirtualAccount(requestIdDtls.getRequestId(), customerEntity);
						customerEntity.setAccountABANumber(ROUTING_NUMBER);
						customerEntity.setExternalAccount(customerEntity.getVirtualAccount());
						customerEntity.setExternalAccountABA(ROUTING_NUMBER);
						log.info(
								"Virtual fineract account created successfully for Direct deposit allocation from Wrapper API");
					}
				} else {
					if (StringUtils.isNotBlank(depositAllocationRequestWrapperModel.getExternalVirtualAccount())
							&& StringUtils.isNotBlank(
									depositAllocationRequestWrapperModel.getExternalVirtualAccountABANumber())) {
						customerEntity
								.setExternalAccount(depositAllocationRequestWrapperModel.getExternalVirtualAccount());
						customerEntity.setExternalAccountABA(
								depositAllocationRequestWrapperModel.getExternalVirtualAccountABANumber());
					} else if (StringUtils.isBlank(customerEntity.getVirtualAccount())
							&& StringUtils.isBlank(customerEntity.getAccountABANumber())) {
						customerEntity = customerServiceHelper
								.createFineractVirtualAccount(requestIdDtls.getRequestId(), customerEntity);
						customerEntity.setAccountABANumber(ROUTING_NUMBER);
						customerEntity.setExternalAccount(customerEntity.getVirtualAccount());
						customerEntity.setExternalAccountABA(ROUTING_NUMBER);
						log.info(
								"Virtual fineract account created successfully for Direct deposit allocation from Wrapper API");
						isFineractAccountCreatedForExistingCustomer = true;
					}
				}
				log.info("Customer entity before break : " + customerEntity);
				break;
			}
			case EMPLOYMENT_VERIFICATION: {
				if (lenderConfigInfo.getPublishEmploymentInfo().equals(StateStatus.NO)) {
					throw new GeneralCustomException("ERROR", "Employment verification is not allowed for the lender");
				}
				if (!employmentVerificationRequestWrapperModel.getCellPhone().startsWith("+1")
						&& employmentVerificationRequestWrapperModel.getCellPhone().length() == 10)
					employmentVerificationRequestWrapperModel
							.setCellPhone("+1".concat(employmentVerificationRequestWrapperModel.getCellPhone()));

				/*
				 * Check if employer selection is done, else make a search and select employer
				 * to update employer details to request table
				 */
				if (requestIdDtls.getEmployer() == null || requestIdDtls.getEmployerPWId() == null) {
					requestIdDtls = getEmployerDetailsBasedOnEmplyerIdFromRequest(
							employmentVerificationRequestWrapperModel.getEmployerId(), requestId, requestIdDtls);
				}
				
				isEmployerPdSupported = customerServiceHelper.checkIfEmployerPdSuported(requestIdDtls);
				
				// VALIDATION PENDING
				customerWrapperAPIService.validateEmploymentVerificationRequest(
						employmentVerificationRequestWrapperModel, requestId, requestIdDtls, lenderConfigInfo);
				customerEntity = checkAndReturnIfCustomerAlreadyExist(customer, lenderConfigInfo, requestId);
				break;
			}
			case INCOME_VERIFICATION: {
				if (lenderConfigInfo.getPublishIncomeInfo().equals(StateStatus.NO)) {
					throw new GeneralCustomException("ERROR", "Income verification is not allowed for the lender");
				}
				if (!incomeVerificationRequestWrapperModel.getCellPhone().startsWith("+1")
						&& incomeVerificationRequestWrapperModel.getCellPhone().length() == 10)
					incomeVerificationRequestWrapperModel
							.setCellPhone("+1".concat(incomeVerificationRequestWrapperModel.getCellPhone()));

				/*
				 * Check if employer selection is done, else make a search and select employer
				 * to update employer details to request table
				 */
				if (requestIdDtls.getEmployer() == null || requestIdDtls.getEmployerPWId() == null) {
					requestIdDtls = getEmployerDetailsBasedOnEmplyerIdFromRequest(
							incomeVerificationRequestWrapperModel.getEmployerId(), requestId, requestIdDtls);
				}
				
				isEmployerPdSupported = customerServiceHelper.checkIfEmployerPdSuported(requestIdDtls);
				
				// VALIDATION PENDING
				customerWrapperAPIService.validateIncomeVerificationRequest(incomeVerificationRequestWrapperModel,
						requestId, requestIdDtls, lenderConfigInfo);
				customerEntity = checkAndReturnIfCustomerAlreadyExist(customer, lenderConfigInfo, requestId);
				break;
			}
			case IDENTITY_VERIFICATION: {
				if (lenderConfigInfo.getPublishIdentityInfo().equals(StateStatus.NO)) {
					throw new GeneralCustomException("ERROR", "Identity verification is not allowed for the lender");
				}
				if (!identityVerificationRequestWrapperModel.getCellPhone().startsWith("+1")
						&& identityVerificationRequestWrapperModel.getCellPhone().length() == 10)
					identityVerificationRequestWrapperModel
							.setCellPhone("+1".concat(identityVerificationRequestWrapperModel.getCellPhone()));

				/*
				 * Check if employer selection is done, else make a search and select employer
				 * to update employer details to request table
				 */
				if (requestIdDtls.getEmployer() == null || requestIdDtls.getEmployerPWId() == null) {
					requestIdDtls = getEmployerDetailsBasedOnEmplyerIdFromRequest(
							identityVerificationRequestWrapperModel.getEmployerId(), requestId, requestIdDtls);
				}
				
				isEmployerPdSupported = customerServiceHelper.checkIfEmployerPdSuported(requestIdDtls);
				
				// VALIDATION PENDING
				customerWrapperAPIService.validateIdentityVerificationRequest(identityVerificationRequestWrapperModel,
						requestId, requestIdDtls, lenderConfigInfo);
				customerEntity = checkAndReturnIfCustomerAlreadyExist(customer, lenderConfigInfo, requestId);
				break;
			}
			default: {
			}
			}

			if (requestIdDtls.getClientName() != null)
				customerEntity.setLender(requestIdDtls.getClientName());
			customerEntity.setEmployer(requestIdDtls.getEmployer());

			log.info("Customer entity before Save : " + customerEntity);
			if (!customerEntity.isExistingCustomer())
				saveCustomer = customerRepository.save(customerEntity);
			else if (isFineractAccountCreatedForExistingCustomer)
				saveCustomer = customerRepository.save(customerEntity);
			else
				saveCustomer = customerEntity;
			saveCustomer.setRequestId(requestId);

			RequestIdDTO requestIdDTO = customerServiceHelper.setRequestIdDetails(saveCustomer,
					customer.getCallbackURLs(), flowType, requestIdDtls, isEmployerPdSupported);
			/* UPDATE REQUEST TABLE WITH CUSTOMERID AND VIRTUAL ACCOUNT NUMBER */

			//PWMVP2-503 || saving customer provided details
			customerServiceHelper.upsertCustomerProvidedDetails(requestId, saveCustomer.getCustomerId(), customer);


			// need to optimize this
			if (flowType.name().equals(FlowTypeEnum.INCOME_VERIFICATION.name())) {
				log.info(" setting number of months ");
				if (incomeVerificationRequestWrapperModel.getNumberOfMonthsRequested() != null) {
					log.info(" setting into request ID ");
					requestIdDTO.setNumberOfMonthsRequested(
							incomeVerificationRequestWrapperModel.getNumberOfMonthsRequested());
				}
			}
			customerServiceHelper.updateRequestIdDetails(requestId, requestIdDTO, identifyProviderServiceUri,
					restTemplate);
			/* CREATE AND SEND SMS AND EMAIL NOTIFICATION */
			// String notificationResponse =
			// createAndSendLinkSMSAndEmailNotification(requestId, requestIdDtls,
			// saveCustomer);
			if (flowType.name().equals(FlowTypeEnum.DEPOSIT_ALLOCATION.name())
					&& directDepositAllocationInstallmentAmount > 0) {
				kafkaPublisherUtil.publishLinkServiceInfo(requestIdDtls, saveCustomer,
						directDepositAllocationInstallmentAmount, flowType, isEmployerPdSupported);
				customer.setInstallmentAmount((int) directDepositAllocationInstallmentAmount);
				checkAndSavePayAllocation(requestIdDtls, customer, flowType,
						depositAllocationRequestWrapperModel.getLoanAmount());
			} else {
				kafkaPublisherUtil.publishLinkServiceInfo(requestIdDtls, saveCustomer,
						(double) customer.getInstallmentAmount(), flowType, isEmployerPdSupported);
				checkAndSavePayAllocation(requestIdDtls, customer, flowType, 0);
			}
			log.info("Customer got created successfully");
		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			log.error("Customerservice createcustomer generalCustomException" + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		} catch (CreateCustomerException e1) {
			Sentry.captureException(e1);
			log.error("Customerservice createcustomer createCustomerException" + e1.getMessage());
			if (virtualAccount != -1)
				throw new CreateCustomerException(e1.getMessage());
		} catch (RequestIdNotFoundException e) {
			Sentry.captureException(e);
			log.error("Customerservice createcustomer RequestIdNotFoundException" + e.getMessage());
			throw new RequestIdNotFoundException(e.getMessage());
		} catch (ServiceNotAvailableException e) {
			Sentry.captureException(e);
			log.error("Customerservice createcustomer ServiceNotAvailableException" + e.getMessage());
			throw new ServiceNotAvailableException(ERROR, e.getMessage());
		} catch (FineractAPIException e) {
			Sentry.captureException(e);
			log.error("Customerservice createcustomer FineractAPIException" + e.getMessage());
			throw new FineractAPIException(e.getMessage());
		} catch (SMSAndEmailNotificationException e) {
			Sentry.captureException(e);
			log.error("Customerservice createcustomer SMSAndEmailNotificationException" + e.getMessage());
			throw new SMSAndEmailNotificationException(e.getMessage());
		}catch (CreateCustomerABAException e) {
			throw e;
		}
		catch (Exception e) {
			Sentry.captureException(e);
			log.error("Customerservice createcustomer Exception" + e.getMessage(),e);
			throw new GeneralCustomException(ERROR, e.getMessage());
		}
		return saveCustomer;
	}

	private Boolean checkABAandVirtualAccountNumber(
			DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel,String requestId) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		log.info("validation started--------------");
		//PWMVP3-88
		String externalAccount = depositAllocationRequestWrapperModel.getExternalVirtualAccount();
		String externalABA = depositAllocationRequestWrapperModel.getExternalVirtualAccountABANumber();
		String encExternalAccount = AESEncryption.encrypt(externalAccount);
		String encExternalABA = AESEncryption.encrypt(externalABA);
		Optional<CustomerDetails> findByExternalAccountAndExternalAccountABA = customerRepository.findByExternalAccountAndExternalAccountABA(encExternalAccount, encExternalABA);
		log.info("validation started=========="+findByExternalAccountAndExternalAccountABA);
		   if(findByExternalAccountAndExternalAccountABA.isPresent()) {
			   log.info("validation started=========="+findByExternalAccountAndExternalAccountABA.get().getExternalAccount()+"==="+findByExternalAccountAndExternalAccountABA.get().getExternalAccountABA());
//			   throw new CreateCustomerABAException("ABA Number and virtual Account number should not be same");
			  if((findByExternalAccountAndExternalAccountABA.get().getPersonalProfile().getCellPhone()
					  .equals(depositAllocationRequestWrapperModel.getCellPhone())) && 
					  ((findByExternalAccountAndExternalAccountABA.get().getExternalAccount()
							  .equals(externalAccount))
					  && (findByExternalAccountAndExternalAccountABA.get().getExternalAccountABA()
							  .equals(externalABA)))){
				   return false;
			   }
			  log.info("ABA and Account Number exists in DB {}",requestId );
			   return true;
		   }else {
			   log.warn("No ABA and Virtual number");
			   return false;
		   }
		
		
	}

	public RequestIdDetails getEmployerDetailsBasedOnEmplyerIdFromRequest(String employerId, String requestId,
			RequestIdDetails requestIdDtls) {
		try {
			EmployerSearchDetailsDTO employerSearchDetailsDTO = customerServiceHelper
					.getEmployerDetailsBasedOnEmployerId(employerId, requestId);
			if (employerSearchDetailsDTO == null
					|| (employerSearchDetailsDTO != null && employerSearchDetailsDTO.getId() == null)) {
				throw new GeneralCustomException("ERROR",
						"Fetch employer details and update request table in create customer failed");
			}
			RequestIdResponseDTO requestResponseDTO = customerServiceHelper.fetchrequestIdDetails(requestId,
					identifyProviderServiceUri, restTemplate);
			if (requestResponseDTO != null)
				requestIdDtls = requestResponseDTO.getData();
			log.info("Employer search and select from create customer :" + employerSearchDetailsDTO);
		} catch (GeneralCustomException e) {
			Sentry.captureException(e);
			log.error("Create customer -  getEmployerDetailsBasedOnEmplyerIdFromRequest Exception" + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		} catch (Exception e) {
			Sentry.captureException(e);
			log.error("Create customer - getEmployerDetailsBasedOnEmplyerIdFromRequest Exception" + e.getMessage());
			throw new GeneralCustomException(ERROR, e.getMessage());
		}
		return requestIdDtls;
	}

	public CustomerDetails checkAndReturnIfCustomerAlreadyExist(CreateCustomerRequest customer,
			LenderConfigInfo lenderConfigInfo, String requestId) {
		log.info("Inside checkAndReturnIfCustomerAlreadyExist :" + customer);
		CustomerDetails customerReponse = new CustomerDetails();
		try {
			Optional<CustomerDetails> byMobileNo = customerRepository
					.findByPersonalProfileCellPhone(customer.getCellPhone());
			if (byMobileNo.isPresent()) {
				log.info("Exsiting customer with new requestID : " + requestId);
				customerReponse = byMobileNo.get();
				customerReponse.setExistingCustomer(true);
				customerReponse.setInstallmentAmount(customer.getInstallmentAmount());
				customerReponse.setNumberOfInstallments(customer.getNumberOfInstallments());

				if (StringUtils.isBlank(customerReponse.getAccountABANumber())
						&& StringUtils.isNotBlank(customerReponse.getVirtualAccount())) {
					customerReponse.setAccountABANumber(ROUTING_NUMBER);
				}
			} else {
				customerReponse = customerServiceHelper.buildCustomerDetails(customer);
				customerReponse.setExistingCustomer(false);
			}
		} catch (Exception e) {
			Sentry.captureException(e);
			throw new GeneralCustomException("ERROR", e.getMessage());
		}
		return customerReponse;
	}

	public double getInstallmentAmount(int iLoanAmount, int iInstallmentAmount, int numberOfInstallment) {
		double loanAmount = commonUtil.convertToDouble(String.valueOf(iLoanAmount));
		double installmentAmount = commonUtil.convertToDouble(String.valueOf(iInstallmentAmount));
		if (installmentAmount > 0) {
			installmentAmount = commonUtil.convertToDouble(String.valueOf(iInstallmentAmount));
		} else {
			installmentAmount = Math.ceil(loanAmount / numberOfInstallment);

		}
		return installmentAmount;
	}

	private boolean checkIfCredentialsVerified(String userId, String credentialType) {
		boolean isCredentialsVerified = false;
		CustomerDetails customerDetailFromDb = customerRepository.findByCustomerId(userId).orElseThrow(
				() -> new CustomerNotFoundException("Customer not present with the customerId : " + userId));
		switch (credentialType) {
		case EMAIL: {
			isCredentialsVerified = VerificationStatusEnum.VERIFIED
					.equals(customerDetailFromDb.getEmailIdVerificationStatus()) ? true : false;
			break;
		}
		case MOBILE: {
			isCredentialsVerified = VerificationStatusEnum.VERIFIED
					.equals(customerDetailFromDb.getCellPhoneVerificationStatus()) ? true : false;
			break;
		}
		default: {
			isCredentialsVerified = false;
		}
		}
		return isCredentialsVerified;
	}

	public CustomerProvidedDetails getCustomerProvidedDetails(String requestId){
		return customerProvidedDetailsRepository.findByRequestId(requestId);
	}

	public String updatePayrollProfileDetails(String customerId, PayrollProviderDetailsDTO payrollProviderDetailsDTO) throws JsonProcessingException {
		PayrollProfileObjectMapper payrollProfileObjectMapper = Mappers.getMapper(PayrollProfileObjectMapper.class);
		PayrollProfile payrollProfile = payrollProfileObjectMapper.convertToProfile(payrollProviderDetailsDTO);
		return customerProvidedDetailsRepository.updatePayrollProfile(customerId, payrollProfile);
	}

	public PayrollProviderDetailsDTO getPayrollProfileDetails(String customerId) {
		PayrollProfile payrollProfile = customerProvidedDetailsRepository.findPayrollProviderDetailsById(customerId);
		PayrollProfileObjectMapper payrollProfileObjectMapper = Mappers.getMapper(PayrollProfileObjectMapper.class);
		PayrollProviderDetailsDTO payrollProviderDetailsDTO = payrollProfileObjectMapper.convertToDTO(payrollProfile);
		return payrollProviderDetailsDTO;
	}

	public ResponseEntity prepareResponse(Object obj, String path) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("data", obj);
		body.put("message", "SUCCESS");
		body.put("timestamp", new Date());
		body.put("path", path);
		return new ResponseEntity<>(body, HttpStatus.OK);
	}

}
