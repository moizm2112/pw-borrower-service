package com.paywallet.userservice.user.constant;

public final class AppConstants {

	public static final String BASE_PATH = "/api/v1";
	public static final String CREATE_USER = "/user";
	public static final String CREATE_CUSTOMER = "/customer/create";
	public static final String GET_CUSTOMER_BY_MOBILENO = "/customer/{mobileNo}";
	public static final String GET_CUSTOMER = "/customer/get/{customerId}";
	public static final String GET_ACCOUNT_DETAILS = "/customer/account/{mobileNo}";
	public static final String VALIDATE_CUSTOMER_ACCOUNT = "/customer/account/validate";
	public static final String UPDATE_CUSTOMER = "/customer/update";

	//=============Validation Errors------------------------
	public static final String FIRST_NAME_VALIDATION_MESSAGE="First Name should contain at least one alphabet.";
	public static final String FIRST_NAME_LENGTH_VALIDATION_MESSAGE="First Name must be greater than 3 character in length";
	public static final String LAST_NAME_VALIDATION_MESSAGE="Last Name should contain at least one alphabet.";
	public static final String MIDDLE_NAME_VALIDATION_MESSAGE="Middle Name should contain at least one alphabet.";
	public static final String LAST_NAME_LENGTH_VALIDATION_MESSAGE="Last Name must be greater than 2 character in length";
	public static final String ADDRESS_VALIDATION_MESSAGE="Address line must contain alphanumeric characters.";
	public static final String CITY_VALIDATION_MESSAGE="City should contain only alphabets";
	public static final String STATE_VALIDATION_MESSAGE="State must contain maximum of 2 characters";
	public static final String ZIP_VALIDATION_MESSAGE="Zip should contain only numeric characters";
	public static final String ZIP_LENGTH_VALIDATION_MESSAGE="Zip must be 5 digits long";
	public static final String LAST4TIN_VALIDATION_MESSAGE="Last4TIN should contain only numeric characters";
	public static final String LAST4TIN_LENGTH_VALIDATION_MESSAGE="Last4TIN must be 4 digits long";
	public static final String FINANCEDAMOUNT_VALIDATION_MESSAGE="Financed Amount should contain only numeric characters";
	public static final String FINANCEDAMOUNT_LENGTH_VALIDATION_MESSAGE="Financed Amount must be at least 1 digits long";
	public static final String BANKABA_VALIDATION_MESSAGE="Bank ABA should contain only numeric characters";
	public static final String BANKABA_LENGTH_VALIDATION_MESSAGE="Bank ABA should be of 9 digits only";
//	public static final String MOBILENO_LENGTH_VALIDATION_MESSAGE ="MobileNo supported only these formats (IND - +911234567890(13 digits) or US - +11234567890(12 digits) format)";
	public static final String MOBILENO_LENGTH_VALIDATION_MESSAGE ="Mobile number supported only these formats (US - +11234567890(12 digits including country code) format)";
	public static final String DOB_FORMAT_VALIDATION_MESSAGE="DOB should be in YYYY-MM-DD format only";

	public static final String FIRST_NAME_NULL_VALIDATION_MESSAGE="First Name Can't be blank.";
	public static final String LAST_NAME_NULL_VALIDATION_MESSAGE="Last Name Can't be blank.";
	public static final String EMAIL_FORMAT_VALIDATION_MESSAGE ="Email is not in valid format.";
	public static final String MOBILENO_FORMAT_VALIDATION_MESSAGE ="Mobile number is not in valid format.";
	public static final String EMAIL_NULL_VALIDATION_MESSAGE ="Email can't be blank.";
	public static final String MOBILENO_NULL_VALIDATION_MESSAGE="Phone Number can't be blank.";
	public static final String SSN_NULL_VALIDATION_MESSAGE="SSN can't be blank.";
	public static final String DOB_NULL_VALIDATION_MESSAGE="DOB can't be blank.";
	public static final String LAST4TIN_NULL_VALIDATION_MESSAGE="Last 4 TIN can't be blank.";
	public static final String ZIP_NULL_VALIDATION_MESSAGE="Zip can't be blank.";
	public static final String STATE_NULL_VALIDATION_MESSAGE="State can't be blank.";
	public static final String CITY_NULL_VALIDATION_MESSAGE="City can't be blank.";
	public static final String ADDRESS_NULL_VALIDATION_MESSAGE="Address can't be blank.";
	public static final String FINANCEDAMOUNT_NULL_VALIDATION_MESSAGE="Financed Amount can't be blank.";
	public static final String EMPLOYER_NULL_VALIDATION_MESSAGE="Employer can't be blank.";
	public static final String LENDER_NULL_VALIDATION_MESSAGE="Lender can't be blank.";
	public static final String BANKABA_NULL_VALIDATION_MESSAGE="Bank ABA can't be blank.";
	public static final String BANKACCOUNTNUMBER_NULL_VALIDATION_MESSAGE="Bank Account number can't be blank.";
	public static final String VIRTUALACCOUNT_NULL_VALIDATION_MESSAGE="Virtual Account can't be blank.";
	
	
	public static final String REQUEST_ID="x-request-id";
	
//===========Till Here--------------------------------------------


}