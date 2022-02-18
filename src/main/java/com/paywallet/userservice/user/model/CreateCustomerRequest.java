package com.paywallet.userservice.user.model;

import com.paywallet.userservice.user.util.CustomerServiceUtil;

import lombok.Data;

@Data
public class CreateCustomerRequest {
    
//	@NotBlank(message = FIRST_NAME_NULL_VALIDATION_MESSAGE)
//    @Pattern(regexp = ".*[a-zA-Z]+.*",message = FIRST_NAME_VALIDATION_MESSAGE)
//    @Size(min = 3,message = FIRST_NAME_LENGTH_VALIDATION_MESSAGE)
    private String firstName;

//    @NotBlank(message = LAST_NAME_NULL_VALIDATION_MESSAGE)
//    @Pattern(regexp = ".*[a-zA-Z]+.*",message = LAST_NAME_VALIDATION_MESSAGE)
//    @Size(min = 2,message = LAST_NAME_LENGTH_VALIDATION_MESSAGE)
    private String lastName;
    
    private String middleName;
    
//    @NotBlank(message = ADDRESSLINE1_NULL_VALIDATION_MESSAGE)
//    @Pattern(regexp = ".*[a-zA-Z0-9]+.*",message = ADDRESS_VALIDATION_MESSAGE)
    private String addressLine1;
    
    private String addressLine2;
    
//    @NotBlank(message = CITY_NULL_VALIDATION_MESSAGE)
//    @Pattern(regexp = "^[a-zA-Z\\u0080-\\u024F\\s\\/\\-\\)\\(\\`\\.\\\"\\']+$",message = CITY_VALIDATION_MESSAGE)
    private String city;
    
//    @NotBlank(message = STATE_NULL_VALIDATION_MESSAGE)
//    @Size(min = 2, message = STATE_VALIDATION_MESSAGE)
//    @StateCheck
    private String state;
    
//    @NotBlank(message = ZIP_NULL_VALIDATION_MESSAGE)
//    @Size(min = 5, max = 5, message = ZIP_LENGTH_VALIDATION_MESSAGE)
//    @Pattern(regexp = "[0-9]+",message = ZIP_VALIDATION_MESSAGE)
    private String zip;

    
//    @NotBlank(message = LAST4TIN_NULL_VALIDATION_MESSAGE)
//    @Pattern(regexp = "[0-9]+",message = LAST4TIN_VALIDATION_MESSAGE)
//    @Size(min = 4, max = 4, message = LAST4TIN_LENGTH_VALIDATION_MESSAGE)
    private String last4TIN;
    
//    @NotBlank(message = DOB_NULL_VALIDATION_MESSAGE)
////    @Pattern(regexp = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|1\\d|2\\d|3[01])\\/(19|20)\\d{2}$", message = DOB_FORMAT_VALIDATION_MESSAGE)
//    @Pattern(regexp = "^(19|20)\\d{2}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", message = DOB_FORMAT_VALIDATION_MESSAGE)
//    @DateCheck
    private String dateOfBirth;
    
//    @Email(message = EMAIL_FORMAT_VALIDATION_MESSAGE)
//    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
//            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",message = EMAIL_FORMAT_VALIDATION_MESSAGE)
//    @NotBlank (message = EMAIL_NULL_VALIDATION_MESSAGE)
    private String emailId;

//    @Pattern(regexp = "^(\\+9?1[0-9]{10})$", message = MOBILENO_FORMAT_VALIDATION_MESSAGE)
//    @Pattern(regexp = "^(\\+\\d{1,2})?\\(?\\d{3}\\)?\\d{3}?\\d{4}$", message = MOBILENO_FORMAT_VALIDATION_MESSAGE)
////    @Size(min = 12, max = 13, message = MOBILENO_LENGTH_VALIDATION_MESSAGE)
//    @Size(min = 10, max = 13, message = MOBILENO_LENGTH_VALIDATION_MESSAGE)
//    @NotBlank  (message = MOBILENO_NULL_VALIDATION_MESSAGE)
    private String mobileNo;
    
    private CallbackURL callbackURLs;
    
//    @JsonInclude(JsonInclude.Include. NON_NULL)
//    @Pattern(regexp = "^(19|20)\\d{2}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", message = FIRSTDATEOFPAYMENT_FORMAT_VALIDATION_MESSAGE)
//    @FirstDateOfPaymentCheck
    private String firstDateOfPayment;
    
//    @RepaymentFrequencyMode(message = REPAYMENT_FREQUENCY_MODE_FORMAT_VALIDATION_MESSAGE)
//    @JsonInclude(JsonInclude.Include. NON_NULL)
    private String repaymentFrequency;
    
//    @JsonInclude(JsonInclude.Include. NON_NULL)
    private int totalNoOfRepayment;
    
//    @JsonInclude(JsonInclude.Include. NON_NULL)
    private int installmentAmount;



    @Override
    public String toString(){
        return new StringBuilder("{")
                .append("firstName").append(firstName).append(",")
                .append("lastName").append(lastName).append(",")
                .append("middleName").append(middleName).append(",")
                .append("addressLine1").append(addressLine1).append(",")
                .append("addressLine2").append(addressLine2).append(",")
                .append("city").append(city).append(",")
                .append("state").append(state).append(",")
                .append("zip").append(zip).append(",")
                .append("last4TIN").append(last4TIN).append(",")
                .append("dateOfBirth").append(dateOfBirth).append(",")
                .append("emailId").append(emailId).append(",")
                .append("mobileNo").append(CustomerServiceUtil.mask(mobileNo)).append(",")
                .append("callbackURLs").append(callbackURLs).append(",")
                .append("firstDateOfPayment").append(firstDateOfPayment).append(",")
                .append("repaymentFrequency").append(repaymentFrequency).append(",")
                .append("totalNoOfRepayment").append(totalNoOfRepayment).append(",")
                .append("installmentAmount").append(installmentAmount).append("}")
                .toString();
    }



}
