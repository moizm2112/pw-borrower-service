package com.paywallet.userservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paywallet.userservice.user.dto.PayrollProviderDetailsDTO;
import com.paywallet.userservice.user.entities.CustomerProvidedDetails;
import com.paywallet.userservice.user.entities.PayrollProfile;
import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.repository.CustomerDetailsRepository;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.CustomerServiceHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTest {

//    @Test
//    void main() {
//    }

    @Autowired
    CustomerServiceHelper customerServiceHelper;
    @Autowired
    CustomerDetailsRepository commonRepository;
    @Autowired
    CustomerService customerService;

    @Test
    public void testCustomerProvidedDetailsAreCreated() throws JsonProcessingException {
        String requestId = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJwYXl3YWxsZXQiLCJpYXQiOjE2NTIyODI2MzQsInN1YiI6ImFjNTVlMmNjLTEzYjItNDJhYy1iMDI2LWQ1OWU1MzJmYWRlNiIsImlzcyI6InRlc3QiLCJleHAiOjE2NjgzNTMwMzR9.ZlAlYNb-CBEvKU0Tb0l0nRE7-vWJkxBtggZJ8ml3dVw";
        String customerId = "123456789";
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setAddressLine1("Street 1 town");
        customerRequest.setAddressLine2("behind post office");
        customerRequest.setCity("bengaluru");
        CustomerProvidedDetails customerProvidedDetails = customerServiceHelper.prepareCustomerProvidedDetails(requestId, customerId, customerRequest);
        CustomerProvidedDetails customerProvidedDetails1 = commonRepository.upsert(customerProvidedDetails);
        Assertions.assertNotNull(customerProvidedDetails1);
    }

    @Test
    public void testGetCustomerProvidedDataByRequestId() {
        String requestId = "ac55e2cc-13b2-42ac-b026-d59e532fade6";
        CustomerProvidedDetails customerProvidedDetails = customerService.getCustomerProvidedDetails(requestId);
        System.out.println(customerProvidedDetails);
        Assertions.assertNotNull(customerProvidedDetails);
    }

    @Test
    public void testUpdatePayrollProvidedData() throws JsonProcessingException {
        String customerId = "62824d0e6c721f6eb79f504f";
        PayrollProfile payrollProfile = new PayrollProfile();
        payrollProfile.setFirstName("Jone");
        payrollProfile.setLastName("Dane");
        payrollProfile.setCellPhone("999999999");
        payrollProfile.setCity("New york");
        String res = commonRepository.updatePayrollProfile(customerId, payrollProfile);
        Assertions.assertEquals(CommonEnum.CUSTOMER_PAY_ROLL_UPDATE_SUCCESS_STATUS_MSG.getMessage(), res);
    }

    @Test
    public void testGetPayrollProviderDetails() {
        String customerId = "62824d0e6c721f6eb79f504f";
        PayrollProviderDetailsDTO payrollProviderDetailsDTO = customerService.getPayrollProfileDetails(customerId);
        System.out.println(payrollProviderDetailsDTO);
        Assertions.assertNotNull(payrollProviderDetailsDTO);
    }

}