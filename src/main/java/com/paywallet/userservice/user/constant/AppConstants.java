package com.paywallet.userservice.user.constant;

public final class AppConstants {

	public static final String BASE_PATH = "/api/v1";
	public static final String CREATE_USER = "/user";
	public static final String CREATE_CUSTOMER = "/customer/create";
	public static final String GET_CUSTOMER_BY_CELLPHONE = "/customer/{cellPhone}";
	public static final String GET_CUSTOMER = "/customer/get/{customerId}";
	public static final String GET_ACCOUNT_DETAILS = "/customer/account/{cellPhone}";
	public static final String VALIDATE_CUSTOMER_ACCOUNT = "/customer/account/validate";
	public static final String UPDATE_CUSTOMER = "/customer/update";
	public static final String UPDATE_CUSTOMER_CELLPHONE = "/customer/update/cellPhone";
	public static final String UPDATE_CUSTOMER_EMAILID = "/customer/update/emailId";
	public static final String ADD_REQUIRED_FIELDS = "/customer/addFields";
	public static final String UPDATE_CUSTOMER_CREDENTIALS = "/customer/contact-info";
	public static final String INITIATE_DEPOSIT_ALLOCATION = "/deposit/update-deposit";
	public static final String INITIATE_EMPLOYMENT_VERIFICATION = "/employment-verification";
	public static final String INITIATE_INCOME_VERIFICATION = "/income-verification";
	public static final String INITIATE_IDENTITY_VERIFICATION = "/identity-verification";
	
	//=============Validation Errors------------------------
	public static final String FIRST_NAME_VALIDATION_MESSAGE="First Name should contain at least one alphabet.";
	public static final String FIRST_NAME_LENGTH_VALIDATION_MESSAGE="First Name must be greater than 3 character in length";
	public static final String LAST_NAME_VALIDATION_MESSAGE="Last Name should contain at least one alphabet.";
	public static final String MIDDLE_NAME_VALIDATION_MESSAGE="Middle Name should contain at least one alphabet.";
	public static final String LAST_NAME_LENGTH_VALIDATION_MESSAGE="Last Name must be greater than 2 character in length";
	public static final String MIDDLE_NAME_LENGTH_VALIDATION_MESSAGE="Middle Name must be greater than 2 character in length";
	public static final String ADDRESS_VALIDATION_MESSAGE="Address line must contain alphanumeric characters.";
	public static final String CITY_VALIDATION_MESSAGE="City should contain only alphabets";
	public static final String STATE_VALIDATION_MESSAGE="State must contain maximum of 2 characters. Eg: For Arizona - AZ";
	public static final String STATE_NOT_VALID_MESSAGE="State entered is not valid. Eg: For Arizona - AZ";
	public static final String ZIP_VALIDATION_MESSAGE="Zip should contain only numeric characters";
	public static final String NO_OF_MONTHS_REQUESTED_VALIDATION_MESSAGE="Number of months requested should contain only numeric characters";
	public static final String ZIP_LENGTH_VALIDATION_MESSAGE="Zip must be 5 digits long";
	public static final String LAST4TIN_VALIDATION_MESSAGE="Last4TIN should contain only numeric characters";
	public static final String LAST4TIN_LENGTH_VALIDATION_MESSAGE="Last4TIN must be 4 digits long";
	public static final String FINANCEDAMOUNT_VALIDATION_MESSAGE="Financed Amount should contain only numeric characters";
	public static final String FINANCEDAMOUNT_LENGTH_VALIDATION_MESSAGE="Financed Amount must be at least 1 digits long";
	public static final String BANKABA_VALIDATION_MESSAGE="Bank ABA should contain only numeric characters";
	public static final String BANKABA_LENGTH_VALIDATION_MESSAGE="Bank ABA should be of 9 digits only";
//	public static final String CELLPHONE_LENGTH_VALIDATION_MESSAGE ="MobileNo supported only these formats (IND - +911234567890(13 digits) or US - +11234567890(12 digits) format)";
	public static final String CELLPHONE_LENGTH_VALIDATION_MESSAGE ="CellPhone Number supported only these formats (US - +11234567890(12 digits including country code) format)";
	public static final String DOB_FORMAT_VALIDATION_MESSAGE="DOB should be in YYYY-MM-DD format only";
	public static final String DOB_INVALID_MESSAGE="You cannot enter DOB in the future ";
	public static final String FIRSTDATEOFPAYMENT_FORMAT_VALIDATION_MESSAGE="First date of payment should be in YYYY-MM-DD format only";
	public static final String REPAYMENT_FREQUENCY_MODE_FORMAT_VALIDATION_MESSAGE="Please enter valid repayment frequency mode";
	public static final String REPAYMENTFREQUENCY_NOT_VALID_MESSAGE="Repayment frequency entered is not valid. Eg: For MONTHLY,WEEKLY,BIWEEKLY, SEMIMONTHLY";
	
	public static final String INSTALLMENTAMOUNT_NULL_VALIDATION_MESSAGE="Installment Amount should be more than zero.";
	public static final String LOANAMOUNT_NULL_VALIDATION_MESSAGE="Loan Amount should be more than zero.";
	public static final String NUMBEROFINSTALLMENTS_NULL_VALIDATION_MESSAGE="Number Of Installments should be more than zero.";
	public static final String INSTALLMENTAMOUNT_MANDATORY_MESSAGE="Installment Amount is mandatory for deposit allocation";
	public static final String NUMBEROFINSTALLMENTS_MANDATORY_MESSAGE="Number Of Installments is mandatory as deposit allocation is active for this lender"; 
	public static final String REPAYMENTFREQUENCY_NULL_VALIDATION_MESSAGE="Repayment frequency can't be blank.";
	public static final String FIRST_NAME_NULL_VALIDATION_MESSAGE="First Name Can't be blank.";
	public static final String FIRSTDATEOFPAYMENT_NULL_VALIDATION_MESSAGE="First date of payment can't be blank.";
	public static final String LAST_NAME_NULL_VALIDATION_MESSAGE="Last Name Can't be blank.";
	public static final String MIDDLE_NAME_NULL_VALIDATION_MESSAGE="Middle Name Can't be blank.";
	public static final String EMAIL_FORMAT_VALIDATION_MESSAGE ="Email is not in valid format.";
	public static final String CELLPHONE_FORMAT_VALIDATION_MESSAGE ="CellPhone Number is not in valid format.";
	public static final String EMAIL_NULL_VALIDATION_MESSAGE ="Email can't be blank.";
	public static final String UPDATING_EMAIL_NULL_VALIDATION_MESSAGE ="Blank email can't be processed.";
	public static final String CELLPHONE_NULL_VALIDATION_MESSAGE="CellPhone Number can't be blank.";
	public static final String EMPLOYERNAME_NULL_VALIDATION_MESSAGE="Employer Name can't be blank.";
	public static final String SSN_NULL_VALIDATION_MESSAGE="SSN can't be blank.";
	public static final String DOB_NULL_VALIDATION_MESSAGE="DOB can't be blank.";
	public static final String LAST4TIN_NULL_VALIDATION_MESSAGE="Last 4 TIN can't be blank.";
	public static final String ZIP_NULL_VALIDATION_MESSAGE="Zip can't be blank.";
	public static final String STATE_NULL_VALIDATION_MESSAGE="State can't be blank.";
	public static final String CITY_NULL_VALIDATION_MESSAGE="City can't be blank.";
	public static final String ADDRESSLINE1_NULL_VALIDATION_MESSAGE="Address Line1 can't be blank.";
	public static final String ADDRESSLINE2_NULL_VALIDATION_MESSAGE="Address Line2 can't be blank.";
	public static final String FINANCEDAMOUNT_NULL_VALIDATION_MESSAGE="Financed Amount can't be blank.";
	public static final String EMPLOYER_NULL_VALIDATION_MESSAGE="Employer can't be blank.";
	public static final String LENDER_NULL_VALIDATION_MESSAGE="Lender can't be blank.";
	public static final String BANKABA_NULL_VALIDATION_MESSAGE="Bank ABA can't be blank.";
	public static final String BANKACCOUNTNUMBER_NULL_VALIDATION_MESSAGE="Bank Account number can't be blank.";
	public static final String VIRTUALACCOUNT_NULL_VALIDATION_MESSAGE="Virtual Account can't be blank.";
	public static final String CALLBACKURL_NULL_VALIDATION_MESSAGE="Virtual Account can't be blank.";
	public static final String CALLBACK_IDENTITYURL_NULL_VALIDATION_MESSAGE="Callback identity url can't be blank.";
	public static final String CALLBACK_EMPLOYMENTURL_NULL_VALIDATION_MESSAGE="Callback employment url can't be blank.";
	public static final String CALLBACK_INCOMEURL_NULL_VALIDATION_MESSAGE="Callback income url can't be blank.";
	public static final String CALLBACK_ALLOCATIONURL_NULL_VALIDATION_MESSAGE="Callback allocation url can't be blank.";
	public static final String CALLBACK_INSUFFICIENTFUNDURL_NULL_VALIDATION_MESSAGE="Callback Insufficient Fund url can't be blank.";
	public static final String CALLBACK_NOTIFICATIONURL_NULL_VALIDATION_MESSAGE="Callback Notification url can't be blank.";
	
	public static final String REQUEST_ID="x-request-id";
	public static final String EMAIL_NOTIFICATION_SUCCESS="Email sent successfully to the provided emailId";
	public static final String EMAIL_NOTIFICATION_FAILED="Provided emailId is not valid";
	public static final String SMS_NOTIFICATION_SUCCESS="SMS sent successfully to the provided cellPhone";
	public static final String SMS_NOTIFICATION_FAILED="Provided cellPhone is not valid";
	public static final String EMAIL_EXIST_VALIDATION_MESSAGE ="Email exist in database. Please provide different email";
	
	public static final String FIRST_NAME_MANDATORY_MESSAGE="First Name can't be made optional";
	public static final String LAST_NAME_MANDATORY_MESSAGE="Last Name can't be made optional";
	public static final String CELLPHONE_MANDATORY_MESSAGE="CellPhone Number can't be made optional";
	public static final String EMAIL_MANDATORY_MESSAGE="Email can't be made optional";
	public static final String LENDER_NAME="lenderName";
	public static final String CALLBACKS_MANDATORY_MESSAGE="Callback URL's can't be made optional";
	
	//WRAPPERAPI
	public static final String EMPLOYERID_MANDATORY_MESSAGE="Employer Id Can't be blank.";
	public static final String ACCOUNT_VALIDATION_OVERRIDE_VALIDATION_MESSAGE="Account validation override can have only YES/NO values";
	public static final String ACH_PULL_REQUEST_VALIDATION_MESSAGE="ACH Pull Request can have only YES/NO values";
	public static final String LENDER_NAME_NO_MATCH="Lender name does not match with the request";
	public static final String EMPLOYERID_NO_MACTH_MESSAGE="Employer Id does not match with the request";
		
//===========Till Here--------------------------------------------


}