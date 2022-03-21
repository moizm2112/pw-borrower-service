package com.paywallet.userservice.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.paywallet.userservice.user.util.AddRequiredFieldsCheck;

import lombok.Data;

@Data
@Document(collection = "customerRequestFields")
public class CustomerRequestFields {
	
	@Id
	private String id;
	
	private String lender;
	
	@AddRequiredFieldsCheck
    private String middleName;
	@AddRequiredFieldsCheck
    private String addressLine1;
	@AddRequiredFieldsCheck
    private String addressLine2;
	@AddRequiredFieldsCheck
    private String city;
	@AddRequiredFieldsCheck
    private String state;
	@AddRequiredFieldsCheck
    private String zip;
	@AddRequiredFieldsCheck
    private String last4TIN;
	@AddRequiredFieldsCheck
    private String dateOfBirth;
	@AddRequiredFieldsCheck
    private String callbackURLs;
	@AddRequiredFieldsCheck
    private String firstDateOfPayment;
	@AddRequiredFieldsCheck
    private String repaymentFrequency;
	@AddRequiredFieldsCheck
    private String numberOfInstallments;
	@AddRequiredFieldsCheck
    private String installmentAmount;
	@AddRequiredFieldsCheck
    private String emailId;
	@AddRequiredFieldsCheck
    private String cellPhone;
	@AddRequiredFieldsCheck
    private String firstName;
	@AddRequiredFieldsCheck
    private String lastName;

}
