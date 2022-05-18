package com.paywallet.userservice.user.constant;

public interface URIConstants {

    String EMP_VERIFICATION_RETRY = "/employment/verification/retry";
    String IDENTITY_VERIFICATION_RETRY = "/identity/verification/retry";
    String INCOME_VERIFICATION_RETRY = "/income/verification/retry";
    String GET_CUSTOMER_PROVIDED_DETAILS = "/customer/provided/details/{requestId}";
    String PATCH_PAYROLL_PROFILE = "/payroll/provided/details/{customerId}";
    String GET_PAYROLL_PROFILE = "/payroll/provided/details/{customerId}";

}
