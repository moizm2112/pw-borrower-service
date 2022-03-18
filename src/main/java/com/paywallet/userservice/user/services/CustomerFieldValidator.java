package com.paywallet.userservice.user.services;

import static com.paywallet.userservice.user.constant.AppConstants.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.paywallet.userservice.user.constant.AppConstants;
import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.RepaymentFrequencyModeEnum;
import com.paywallet.userservice.user.enums.StateEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.ServiceNotAvailableException;
import com.paywallet.userservice.user.model.CallbackURL;
import com.paywallet.userservice.user.model.LenderConfigInfo;
import com.paywallet.userservice.user.repository.CustomerRepository;
import com.paywallet.userservice.user.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerFieldValidator {

	@Value("${admin.service.url}")
	private String adminServiceUri;
	
	@Autowired
	CommonUtil commonUtil;
	
	public List<String> validateFirstName(String firstName) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(firstName)) {
			errorList.add(FIRST_NAME_NULL_VALIDATION_MESSAGE);
		}
		String regex = ".*[a-zA-Z]+.*";
		if (!checkFieldForValidPattern(regex, firstName)) {
			errorList.add(FIRST_NAME_VALIDATION_MESSAGE);
		}
		if (firstName != null && StringUtils.length(firstName) < 3) {
			errorList.add(FIRST_NAME_LENGTH_VALIDATION_MESSAGE);
		}
		return errorList;
	}
	
	public List<String> validateLastName(String lastName) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(lastName)) {
			errorList.add(LAST_NAME_NULL_VALIDATION_MESSAGE);
		}
		String regex = ".*[a-zA-Z]+.*";
		if (!checkFieldForValidPattern(regex, lastName)) {
			errorList.add(LAST_NAME_VALIDATION_MESSAGE);
		}
		if (lastName != null && StringUtils.length(lastName) < 2) {
			errorList.add(LAST_NAME_LENGTH_VALIDATION_MESSAGE);
		}
		return errorList;
	}
	
	public List<String> validateMobileNo(String mobileNo) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(mobileNo)) {
			errorList.add(MOBILENO_NULL_VALIDATION_MESSAGE);
		}
		String regex = "^(\\+\\d{1,2})?\\(?\\d{3}\\)?\\d{3}?\\d{4}$";
		if (!checkFieldForValidPattern(regex, mobileNo)) {
			errorList.add(MOBILENO_FORMAT_VALIDATION_MESSAGE);
		}
		if (mobileNo != null && !(StringUtils.length(mobileNo) >= 10 && StringUtils.length(mobileNo) <= 13)) {
			errorList.add(MOBILENO_LENGTH_VALIDATION_MESSAGE);
		}
		return errorList;
	}

	public List<String> validateMiddleName(String middleName) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(middleName)) {
			errorList.add(MIDDLE_NAME_NULL_VALIDATION_MESSAGE);
		}
		String regex = ".*[a-zA-Z]+.*";
		if (!checkFieldForValidPattern(regex, middleName)) {
			errorList.add(MIDDLE_NAME_VALIDATION_MESSAGE);
		}
		if (middleName != null && StringUtils.length(middleName) < 2) {
			errorList.add(MIDDLE_NAME_LENGTH_VALIDATION_MESSAGE);
		}
		return errorList;
	}

	public List<String> validateAddressLine1(String addressLine1) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(addressLine1)) {
			errorList.add(ADDRESSLINE1_NULL_VALIDATION_MESSAGE);
		}
		String regex = ".*[a-zA-Z0-9]+.*";
		if (!checkFieldForValidPattern(regex, addressLine1)) {
			errorList.add(ADDRESS_VALIDATION_MESSAGE);
		}
		return errorList;
	}

	public List<String> validateAddressLine2(String addressLine2) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(addressLine2)) {
			errorList.add(ADDRESSLINE2_NULL_VALIDATION_MESSAGE);
		}
		String regex = ".*[a-zA-Z0-9]+.*";
		if (!checkFieldForValidPattern(regex, addressLine2)) {
			errorList.add(ADDRESS_VALIDATION_MESSAGE);
		}
		return errorList;
	}

	public List<String> validateCity(String city) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(city)) {
			errorList.add(CITY_NULL_VALIDATION_MESSAGE);
		}
		String regex = "^[a-zA-Z\\u0080-\\u024F\\s\\/\\-\\)\\(\\`\\.\\\"\\']+$";
		if (!checkFieldForValidPattern(regex, city)) {
			errorList.add(CITY_VALIDATION_MESSAGE);
		}
		return errorList;
	}

	public List<String> validateState(String state) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(state)) {
			errorList.add(STATE_NULL_VALIDATION_MESSAGE);
		}
		String regex = "^[a-zA-Z\\u0080-\\u024F\\s\\/\\-\\)\\(\\`\\.\\\"\\']+$";
		if (!checkFieldForValidPattern(regex, state)) {
			errorList.add(STATE_VALIDATION_MESSAGE);
		}
		if (state != null && !EnumUtils.isValidEnum(StateEnum.class, state))
			errorList.add(STATE_NOT_VALID_MESSAGE);

		return errorList;
	}
	
	public List<String> validateDateOfBirth(String dateOfBirth) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(dateOfBirth)) {
			errorList.add(DOB_NULL_VALIDATION_MESSAGE);
		}
		String regex = "^(19|20)\\d{2}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
		if (!checkFieldForValidPattern(regex, dateOfBirth)) {
			errorList.add(DOB_FORMAT_VALIDATION_MESSAGE);
		}
		try {
			if(!dateOfBirthCheck(dateOfBirth))
				errorList.add(DOB_INVALID_MESSAGE);
		}catch(GeneralCustomException e) {
			errorList.add(e.getMessage());
		}

		return errorList;
	}

	public List<String> validateZip(String zip) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(zip)) {
			errorList.add(ZIP_NULL_VALIDATION_MESSAGE);
		}
		String regex = "[0-9]+";
		if (!checkFieldForValidPattern(regex, zip)) {
			errorList.add(ZIP_VALIDATION_MESSAGE);
		}
		if (StringUtils.length(zip) != 5)
			errorList.add(ZIP_LENGTH_VALIDATION_MESSAGE);
		return errorList;
	}

	public List<String> validateLast4TIN(String last4TIN) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(last4TIN)) {
			errorList.add(LAST4TIN_NULL_VALIDATION_MESSAGE);
		}
		String regex = "[0-9]+";
		if (!checkFieldForValidPattern(regex, last4TIN)) {
			errorList.add(LAST4TIN_VALIDATION_MESSAGE);
		}
		if (StringUtils.length(last4TIN) != 4)
			errorList.add(LAST4TIN_LENGTH_VALIDATION_MESSAGE);
		return errorList;
	}
	
	public List<String> validateFirstDateOfPayment(String firstDateOfPayment, String lender) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(firstDateOfPayment)) {
			errorList.add(FIRSTDATEOFPAYMENT_NULL_VALIDATION_MESSAGE);
		}
		String regex = "^(19|20)\\d{2}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
		if (!checkFieldForValidPattern(regex, firstDateOfPayment)) {
			errorList.add(FIRSTDATEOFPAYMENT_FORMAT_VALIDATION_MESSAGE);
		}
		try {
			commonUtil.checkIfFirstDateOfPaymentValid(firstDateOfPayment, lender);
		}
		catch(GeneralCustomException e) {
			errorList.add(e.getMessage());
		}catch(Exception e) {
			errorList.add(e.getMessage());
		}
		
		return errorList;
	}
	
	public List<String> validateTotalNoOfRepayment(Integer totalNoOfRepayment) {
		List<String> errorList = new ArrayList<String>();
		if (totalNoOfRepayment == null || totalNoOfRepayment <= 0) {
			errorList.add(TOTALNOOFREPAYMENT_NULL_VALIDATION_MESSAGE);
		}
		return errorList;
	}
	
	public List<String> validateInstallmentAmount(Integer installmentAmount) {
		List<String> errorList = new ArrayList<String>();
		if (installmentAmount == null || installmentAmount <= 0) {
			errorList.add(INSTALLMENTAMOUNT_NULL_VALIDATION_MESSAGE);
		}
		return errorList;
	}
	
	public List<String> validateRepaymentFrequency(String repaymentFrequency) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(repaymentFrequency)) {
			errorList.add(REPAYMENTFREQUENCY_NULL_VALIDATION_MESSAGE);
		}
		if(!StringUtils.isAllUpperCase(repaymentFrequency)) {
			repaymentFrequency = StringUtils.toRootUpperCase(repaymentFrequency);
		}
		if(repaymentFrequency != null && !EnumUtils.isValidEnum(RepaymentFrequencyModeEnum.class, repaymentFrequency))
			errorList.add(REPAYMENTFREQUENCY_NOT_VALID_MESSAGE);
		
		return errorList;
	}

	public List<String> validateEmailId(String emailId, CustomerRepository customerRepository, String mobileNo) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(emailId)) {
			errorList.add(EMAIL_NULL_VALIDATION_MESSAGE);
		}
		String regex = "^(?=.{1,64}@)[A-Za-z0-9+_-]+(\\.[A-Za-z0-9+_-]+)*@"
				+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		if (!checkFieldForValidPattern(regex, emailId)) {
			errorList.add(EMAIL_FORMAT_VALIDATION_MESSAGE);
		}
		try {
			if(emailId != null && mobileNo != null) {
				Optional<CustomerDetails> byMobileNo = customerRepository.findByPersonalProfileMobileNo(mobileNo);
		        if (!byMobileNo.isPresent()) {
		        	Optional<CustomerDetails> checkForEmailIdInDB = customerRepository
							.findByPersonalProfileEmailId(emailId);
					if(checkForEmailIdInDB.isPresent()) {
						errorList.add(EMAIL_EXIST_VALIDATION_MESSAGE);
					}
		        }
			}
		}catch(Exception e) {
			errorList.add(EMAIL_EXIST_VALIDATION_MESSAGE);
			return errorList;
		}
		return errorList;
	}
	
	public List<String> validateCallbackURLs(CallbackURL callBackURL, RestTemplate restTemplate, String requestId, String lender, LenderConfigInfo lenderConfigInfo) {
		List<String> errorList = new ArrayList<String>();
		if (callBackURL == null) {
			errorList.add(CALLBACKURL_NULL_VALIDATION_MESSAGE);
		}
		else
		{
			if(lenderConfigInfo.getPublishIdentityInfo().name().equals("YES") && !(callBackURL.getIdentityCallbackUrls() != null && ((ArrayList<String>)callBackURL.getIdentityCallbackUrls()).size() > 0 
					&& !checkForEmptyStringInArray(callBackURL.getIdentityCallbackUrls()))) {
				errorList.add(CALLBACK_IDENTITYURL_NULL_VALIDATION_MESSAGE);
			}
			if(lenderConfigInfo.getPublishEmploymentInfo().name().equals("YES") && !(callBackURL.getEmploymentCallbackUrls() != null && ((ArrayList<String>)callBackURL.getEmploymentCallbackUrls()).size() > 0
					&& !checkForEmptyStringInArray(callBackURL.getEmploymentCallbackUrls()))) {
				errorList.add(CALLBACK_EMPLOYMENTURL_NULL_VALIDATION_MESSAGE);
			}
			if(lenderConfigInfo.getPublishIncomeInfo().name().equals("YES") && !(callBackURL.getIncomeCallbackUrls() != null && ((ArrayList<String>)callBackURL.getIncomeCallbackUrls()).size() > 0
					&& !checkForEmptyStringInArray(callBackURL.getIncomeCallbackUrls()))) {
				errorList.add(CALLBACK_INCOMEURL_NULL_VALIDATION_MESSAGE);
			}
			if(lenderConfigInfo.getInvokeAndPublishDepositAllocation().name().equals("YES") && !(callBackURL.getAllocationCallbackUrls() != null && ((ArrayList<String>)callBackURL.getAllocationCallbackUrls()).size() > 0
					&& !checkForEmptyStringInArray(callBackURL.getAllocationCallbackUrls()))) {
				errorList.add(CALLBACK_ALLOCATIONURL_NULL_VALIDATION_MESSAGE);
			}
			if(lenderConfigInfo.getValidateAffordabilityCheck().name().equals("YES") && !(callBackURL.getInsufficientFundCallbackUrls() != null && ((ArrayList<String>)callBackURL.getInsufficientFundCallbackUrls()).size() > 0
					&& !checkForEmptyStringInArray(callBackURL.getInsufficientFundCallbackUrls()))) {
				errorList.add(CALLBACK_INSUFFICIENTFUNDURL_NULL_VALIDATION_MESSAGE);
			}
			if(!(callBackURL.getNotificationUrls() != null && ((ArrayList<String>)callBackURL.getNotificationUrls()).size() > 0
					&& !checkForEmptyStringInArray(callBackURL.getNotificationUrls()))) {
				errorList.add(CALLBACK_NOTIFICATIONURL_NULL_VALIDATION_MESSAGE);
			}
		}
		return errorList;
	}
	
	public List<String> validateEmployerId(String employerId, String employerPWId) {
		List<String> errorList = new ArrayList<String>();
		if (StringUtils.isBlank(employerId)) {
			errorList.add(EMPLOYERID_MANDATORY_MESSAGE);
		}
		if(employerPWId != null && !employerId.equalsIgnoreCase(employerPWId)) {
			errorList.add(EMPLOYERID_NO_MACTH_MESSAGE);
		}
		return errorList;
	}
	
	public List<String> validateACHPullRequest(String achPullRequest) {
		List<String> errorList = new ArrayList<String>();
		if (!StringUtils.containsAny(achPullRequest,new String[]{"YES", "NO"})) {
			errorList.add(ACH_PULL_REQUEST_VALIDATION_MESSAGE);
		}
		return errorList;
	}
	
	public List<String> validateExternalVirtualAccountABANumber(String externalVirtualAccountABANumber) {
		List<String> errorList = new ArrayList<String>();
		String regex = "[0-9]+";
		if (!checkFieldForValidPattern(regex, externalVirtualAccountABANumber)) {
			errorList.add(BANKABA_VALIDATION_MESSAGE);
		}
		if (externalVirtualAccountABANumber != null && StringUtils.length(externalVirtualAccountABANumber) != 9) {
			errorList.add(BANKABA_LENGTH_VALIDATION_MESSAGE);
		}
		return errorList;
	}
	
	public List<String> validateAccountValidationOverride(String accountValdiationOverride) {
		List<String> errorList = new ArrayList<String>();
		if (!StringUtils.containsAny(accountValdiationOverride,new String[]{"YES", "NO"})) {
			errorList.add(ACCOUNT_VALIDATION_OVERRIDE_VALIDATION_MESSAGE);
		}
		return errorList;
	}
	
	public List<String> validateNoOfMonthsRequested(String noOfMonthsRequested) {
		List<String> errorList = new ArrayList<String>();
		String regex = "[0-9]+";
		if (!checkFieldForValidPattern(regex, noOfMonthsRequested)) {
			errorList.add(NO_OF_MONTHS_REQUESTED_VALIDATION_MESSAGE);
		}
		return errorList;
	}
	
	
	public boolean checkForEmptyStringInArray(List<String> lsCallBackUrls) {
		
		if(lsCallBackUrls.size() > 0) {
			for (String string : lsCallBackUrls) {
				if(StringUtils.isBlank(string))
					return true;
			}
		}
		return false;
	}

	public boolean checkFieldForValidPattern(String regexPattern, String fieldValue) {
		if(fieldValue != null) {
			Pattern pattern = Pattern.compile(regexPattern);
			Matcher m = pattern.matcher(fieldValue);
			return m.matches();
		}
		return false;
	}
	
	public boolean dateOfBirthCheck(String date) {
		boolean valid = false;
        try {
        	if(date != null) {
        		// ResolverStyle.STRICT for 30, 31 days checking, and also leap year.
        		LocalDate.parse(date,
                         DateTimeFormatter.ofPattern("uuuu-M-d")
                                 .withResolverStyle(ResolverStyle.STRICT));
        		 
        		Date currentDate = new Date();  
         	    // First Date Of Payment
         		Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(date);
         		// Current Date
     			Date currentDateFormated = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(currentDate));
     			
     			if(dateOfBirth.before(currentDateFormated))
     				valid = true;
     			else
     				valid = false;
        	}else {
        		valid = true;
        	}
        } catch (DateTimeParseException e) {
        	valid = false;
        	throw new GeneralCustomException("ERROR", e.getMessage());
            
        } catch (ParseException e) {
        	valid = false;
        	throw new GeneralCustomException("ERROR", e.getMessage());
		}
        return valid;	
	}
	
	/**
     * Method fetches the lender configuration details by lender name.
     * @param requestId
     * @return
     * @throws ResourceAccessException
     * @throws GeneralCustomException
     */
    public LenderConfigInfo fetchLenderConfigurationForCallBack(String requestId, RestTemplate restTemplate, String lenderName) {
		log.info("request id :: " + requestId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(REQUEST_ID, requestId);
		LenderConfigInfo lenderConfigInfo = new LenderConfigInfo();
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		try {
			log.info("adminServiceUri:: " + adminServiceUri);

			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(adminServiceUri).queryParam(AppConstants.LENDER_NAME, lenderName).encode();
			log.info("uriBuilder url formed:: " + uriBuilder.toUriString());
			lenderConfigInfo = restTemplate
					.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, LenderConfigInfo.class)
					.getBody();

		} catch (ResourceAccessException resourceException) {
			throw new ServiceNotAvailableException(HttpStatus.SERVICE_UNAVAILABLE.toString(), resourceException.getMessage());
		} catch (Exception ex) {
			throw new GeneralCustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),ex.getMessage());
		}
		return lenderConfigInfo;
	}

}
