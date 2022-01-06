package com.paywallet.userservice.user.helper;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.entities.PersonalProfile;
import com.paywallet.userservice.user.entities.SalaryProfile;
import com.paywallet.userservice.user.model.AccountDetails;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.CustomerAccountResponseDTO;
import com.paywallet.userservice.user.model.CustomerResponseDTO;
import com.paywallet.userservice.user.model.UpdateCustomerRequestDTO;
import com.paywallet.userservice.user.model.ValidateAccountRequest;

public class CustomerDataTest {
    public CustomerAccountResponseDTO getAccDetails(){
    	CustomerAccountResponseDTO custAccountResponse = new CustomerAccountResponseDTO();
        AccountDetails accountDetailsTestData = new AccountDetails();
        accountDetailsTestData.setAbaOfSalaryAccount("122199983");
        accountDetailsTestData.setSalaryAccountNumber("89455");
        custAccountResponse.setData(accountDetailsTestData);
        return custAccountResponse;
    }

    public CreateCustomerRequest createCustomerRequest(){
        CreateCustomerRequest createCustomerRequestTest = new CreateCustomerRequest();
        createCustomerRequestTest.setMobileNo("+919980024111");
        createCustomerRequestTest.setFirstName("Wilson");
        createCustomerRequestTest.setLastName("Paul");
        createCustomerRequestTest.setMiddleName("");
        createCustomerRequestTest.setEmailId("Wilson@gmail.com");
        createCustomerRequestTest.setAddressLine1("15 jackon street");
        createCustomerRequestTest.setAddressLine2("");
        createCustomerRequestTest.setZip("33333");
        createCustomerRequestTest.setCity("AnyCity");
        createCustomerRequestTest.setState("OK");
        createCustomerRequestTest.setLast4TIN("1512");
        createCustomerRequestTest.setDateOfBirth("2002-10-10");
        createCustomerRequestTest.setFinancedAmount("15");
//        createCustomerRequestTest.setEmployer("15");
//        createCustomerRequestTest.setLender("15");
        createCustomerRequestTest.setBankABA("122199983");
        createCustomerRequestTest.setBankAccountNumber("15");

        return createCustomerRequestTest;
    }

    public CustomerResponseDTO createCustomerResponse(){
        CustomerResponseDTO createCustomerResponseTest = new CustomerResponseDTO();
        CustomerDetails customerDetails = new CustomerDetails();
        PersonalProfile personalProfile =  new PersonalProfile();
        customerDetails.setCustomerId("61822f23019cba309dd5b070");
        personalProfile.setMobileNo("+919980024111");
        customerDetails.setPersonalProfile(personalProfile);
        customerDetails.setExistingCustomer(false);
        createCustomerResponseTest.setData(customerDetails);
        return createCustomerResponseTest;

    }

    public ValidateAccountRequest validateAccountRequest(){
        ValidateAccountRequest validateAccountRequestTest = new ValidateAccountRequest();
        validateAccountRequestTest.setMobileNo("+919980024111");
        validateAccountRequestTest.setAbaOfSalaryAccount("122199983");
        validateAccountRequestTest.setSalaryAccountNumber("89455");

        return validateAccountRequestTest;
    }

    public CustomerResponseDTO validateAccountResponse(){
    	CustomerResponseDTO validateAccountResponseTest = new CustomerResponseDTO();
    	CustomerDetails customerDetails = new CustomerDetails();
    	customerDetails.setStatus("Accept");
    	customerDetails.setSalaryAccountNumber("89455");
    	customerDetails.setAbaOfSalaryAccount("122199983");
    	validateAccountResponseTest.setData(customerDetails);
        return validateAccountResponseTest;
    }

    public UpdateCustomerRequestDTO updateCustomerRequest(){
        UpdateCustomerRequestDTO updateCustomerRequesttest = new UpdateCustomerRequestDTO();
        updateCustomerRequesttest.setMobileNo("+919980024111");
        SalaryProfile salaryProfileTest = new SalaryProfile();
        salaryProfileTest.setGrossSalary("500.34");
        salaryProfileTest.setNetSalary("265.4");
        salaryProfileTest.setSalaryAccount("89455");
        salaryProfileTest.setAba("122199983");
        salaryProfileTest.setSalaryFrequency("semimonthly");
        salaryProfileTest.setProvider("ARGYLE");
        salaryProfileTest.setToken("1739640f-7fb9-4386-9561-817aa33e172f");
        updateCustomerRequesttest.setSalaryProfile(salaryProfileTest);

        return updateCustomerRequesttest;

    }

    public CustomerResponseDTO updateCustomerResponse(){
    	CustomerResponseDTO createCustomerResponseTest = new CustomerResponseDTO();
        CustomerDetails updateCustomertest = new CustomerDetails();
        updateCustomertest.setCustomerId("61822f23019cba309dd5b070");
        PersonalProfile personalProfileTest = new PersonalProfile();
        personalProfileTest.setMobileNo("+919980024111");
        personalProfileTest.setFirstName("Wilson");
        personalProfileTest.setLastName("Paul");
        personalProfileTest.setEmailId("Wilson@gmail.com");
        updateCustomertest.setPersonalProfile(personalProfileTest);
        SalaryProfile salaryProfileTest = new SalaryProfile();
        salaryProfileTest.setGrossSalary("500.34");
        salaryProfileTest.setNetSalary("265.4");
        salaryProfileTest.setSalaryAccount("89455");
        salaryProfileTest.setAba("122199983");
        salaryProfileTest.setSalaryFrequency("semimonthly");
        salaryProfileTest.setProvider("ARGYLE");
        salaryProfileTest.setToken("1739640f-7fb9-4386-9561-817aa33e172f");
        updateCustomertest.setSalaryProfile(salaryProfileTest);
        updateCustomertest.setUpdateCounter(0);
        updateCustomertest.setStatus("Accept");
        updateCustomertest.setSalaryAccountNumber("89455");
        updateCustomertest.setAbaOfSalaryAccount("122199983");
//        updateCustomertest.setEmployer("Amazon");
//        updateCustomertest.setLender("Argyle");
        createCustomerResponseTest.setData(updateCustomertest);
        return createCustomerResponseTest;

    }
}
