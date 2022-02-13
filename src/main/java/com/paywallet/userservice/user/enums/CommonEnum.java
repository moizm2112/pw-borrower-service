package com.paywallet.userservice.user.enums;

public enum CommonEnum {
	
	CUSTOMER_DATA_NOT_FOUND_MSG("Customer with given mobile number does not exist in the database"),
	REQUESTID_NOT_FOUND_MSG("Given requestId does not exists"),
	CREATE_CUSTOMER_FAILED("Create Customer Failed"),
	CUSTOMER_ACCOUNT_ERROR_MSG("Customer Account does not exists"),
	SERVICE_UNAVAILABLE("Service Unavailable"),

	CUSTOMER_CREATED_SUCCESS_MSG("Customer created successfully"),
	CUSTOMER_EXIST_SUCCESS_MSG("Customer already exist for the given mobileNo in database"),
    SUCCESS_STATUS_MSG("SUCCESS"),
    UPDATE_MOBILENO_SUCCESS_STATUS_MSG("Customer Mobile number updated Successfully"),
    UPDATE_EMAILID_SUCCESS_STATUS_MSG("Customer Email updated Successfully"),
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
