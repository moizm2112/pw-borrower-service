package com.paywallet.userservice.user.constant;

public interface RetryAPIConstants {
    String COMPLETED = "Completed";

    //login
    String LOGIN_NOT_COMPLETED_MESSAGE = "SDK login is not completed, we can allow retry API ";
    String LOGIN_COMPLETED_MESSAGE = "Customer has already completed the Employer Login. Retry not allowed";

    // request ID
    String REQUEST_ID = "Request ID :";

    // Notification
    String INVALID_SMS_ERROR_CODE_KEY = "ErrorCode";
    String INVALID_SMS_ERROR_CODE_VALUE = "30003";
    String INVALID_EMAIL_ERROR_CODE_VALUE1 = "bounce";
    String INVALID_EMAIL_ERROR_CODE_VALUE2 = "dropped";

    // notification
    String INVALID_EMAIL_MOBILE_MESSAGE = "Previous allocation notification is failed with Invalid EmailID and Invalid Mobile No., please update the EmailID and Mobile";
    String NOT_BOTH_INVALID_EMAIL_MOBILE_MESSAGE = "Previous allocation notification is not failed with both Invalid EmailID and Invalid Mobile No., we can allow retry API";

}
