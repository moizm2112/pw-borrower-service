package com.paywallet.userservice.user.enums;

public enum CommonEnum {
	
	CUSTOMER_DATA_NOT_FOUND_MSG("Customer with given CellPhone Number does not exist in the database"),
	REQUESTID_NOT_FOUND_MSG("Given requestId does not exists"),
	CREATE_CUSTOMER_FAILED("Create Customer Failed"),
	CUSTOMER_ACCOUNT_ERROR_MSG("Customer Account does not exists"),
	SERVICE_UNAVAILABLE("Service Unavailable"),

	CUSTOMER_CREATED_SUCCESS_MSG("Customer created successfully"),
	CUSTOMER_EXIST_SUCCESS_MSG("Request linked to the existing customer successfully"),
    SUCCESS_STATUS_MSG("SUCCESS"),
    UPDATE_CELLPHONE_SUCCESS_STATUS_MSG("Customer CellPhone Number updated Successfully"),
    UPDATE_EMAILID_SUCCESS_STATUS_MSG("Customer Email updated Successfully"),
    UPDATE_CUSTOMER_CREDENTIALS_SUCCESS_STATUS_MSG("Customer credentials updated successfully"),
    DEPOSIT_ALLOCATION_SUCCESS_STATUS_MSG("Deposit allocation process initiated"),
    EMPLOYMENT_VERIFICATION_SUCCESS_STATUS_MSG("Employment verification process initiated"),
    INCOME_VERIFICATION_SUCCESS_STATUS_MSG("Income verification process initiated"),
    IDENTITY_VERIFICATION_SUCCESS_STATUS_MSG("Identity verification process initiated"),
    CUSTOMER_CREATED("CUSTOMER_CREATED"),
    PAY_CYCLE("paycycle"),
    FAILED_STATUS_MSG("FAILED"),
    COMMON_RETRY_FALED_MSG("Customer has already completed the Employer Login. Retry not allowed"),
    COMMON_RETRY_SUCCESS_MSG("Link resend to the customer has been initiated."),

    SUCCESS_HTTP_RESPONSE_MSG(200,"Success"),
    CUSTOMER_PAY_ROLL_UPDATE_SUCCESS_STATUS_MSG("Customer payroll profile updated successfully"),
    CUSTOMER_PAY_ROLL_UPDATE_FAILED_STATUS_MSG("Customer payroll profile update failed"),
    ;
	
    private String message;
    private int code;
    CommonEnum(String message){
        this.message = message;
    }
    CommonEnum(int code,String message){
        this.message = message;
        this.code = code;
    }
    CommonEnum(){}
    public String getMessage(){
        return this.message;
    }
    public int getCode(){
        return this.code;
    }

}
