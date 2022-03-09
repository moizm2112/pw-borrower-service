package com.paywallet.userservice.user.enums;

public enum CommonEnum {
	
	CUSTOMER_DATA_NOT_FOUND_MSG("Customer with given mobile number does not exist in the database"),
	REQUESTID_NOT_FOUND_MSG("Given requestId does not exists"),
	CREATE_CUSTOMER_FAILED("Create Customer Failed"),
	CUSTOMER_ACCOUNT_ERROR_MSG("Customer Account does not exists"),
	SERVICE_UNAVAILABLE("Service Unavailable"),

	CUSTOMER_CREATED_SUCCESS_MSG("Customer created successfully"),
	CUSTOMER_EXIST_SUCCESS_MSG("Request linked to the existing customer successfully"),
    SUCCESS_STATUS_MSG("SUCCESS"),
    UPDATE_MOBILENO_SUCCESS_STATUS_MSG("Customer Mobile number updated Successfully"),
    UPDATE_EMAILID_SUCCESS_STATUS_MSG("Customer Email updated Successfully"),
    UPDATE_CUSTOMER_CREDENTIALS_SUCCESS_STATUS_MSG("Customer credentials updated successfully"),
    DEPOSIT_ALLOCATION_SUCCESS_STATUS_MSG("Deposit allocation success"),
    CUSTOMER_CREATED("CUSTOMER_CREATED"),
    PAY_CYCLE("paycycle"),
    FAILED_STATUS_MSG("FAILED"),
    

    SUCCESS_HTTP_RESPONSE_MSG(200,"Success");
	
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
