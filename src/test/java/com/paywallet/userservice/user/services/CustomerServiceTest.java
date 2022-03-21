package com.paywallet.userservice.user.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.helper.CustomerDataTest;
import com.paywallet.userservice.user.model.CustomerAccountResponseDTO;
import com.paywallet.userservice.user.model.CustomerResponseDTO;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationRequestWrapperModel;

class CustomerServiceTest {

    @Test
    void getAccountDetails() {

        CustomerDataTest customerDataTest = new CustomerDataTest();
        CustomerAccountResponseDTO accDetails = customerDataTest.getAccDetails();
        String cellPhone="+919980024111";
        CustomerService mockCustomerService = mock(CustomerService.class);
        when(mockCustomerService.getAccountDetails(cellPhone)).thenReturn(accDetails.getData());

        assertEquals("89455",accDetails.getData().getSalaryAccountNumber());
        assertEquals("122199983",accDetails.getData().getAccountABANumber());
    }

    @Test
    void createCustomer() {
        CustomerDataTest customerDataTest = new CustomerDataTest();
        CustomerResponseDTO customerResponse = customerDataTest.createCustomerResponse();
        CustomerService mockCustomerService = mock(CustomerService.class);
        DepositAllocationRequestWrapperModel depositAllocationRequest = new DepositAllocationRequestWrapperModel();
        when(mockCustomerService.createCustomer(customerDataTest.createCustomerRequest(),"",depositAllocationRequest,FlowTypeEnum.GENERAL))
        		.thenReturn(customerResponse.getData());

        assertEquals("61822f23019cba309dd5b070",customerResponse.getData().getCustomerId());
        assertEquals("+919980024111",customerResponse.getData().getPersonalProfile().getCellPhone());
    }
    
    @Test
    void getCustomer() {
        CustomerDataTest customerDataTest = new CustomerDataTest();
        CustomerResponseDTO customerResponse = customerDataTest.createCustomerResponse();
        String customerId="61822f23019cba309dd5b070";
        CustomerService mockCustomerService = mock(CustomerService.class);
        when(mockCustomerService.getCustomer(customerId))
        		.thenReturn(customerResponse.getData());

        assertEquals("61822f23019cba309dd5b070",customerResponse.getData().getCustomerId());
        assertEquals("+919980024111",customerResponse.getData().getPersonalProfile().getCellPhone());
    }
    
    @Test
    void getCustomerByMobileNo() {
        CustomerDataTest customerDataTest = new CustomerDataTest();
        CustomerResponseDTO customerResponse = customerDataTest.createCustomerResponse();
        String cellPhone="+919980024111";
        CustomerService mockCustomerService = mock(CustomerService.class);
        when(mockCustomerService.getCustomerByMobileNo(cellPhone))
        		.thenReturn(customerResponse.getData());

        assertEquals("61822f23019cba309dd5b070",customerResponse.getData().getCustomerId());
        assertEquals("+919980024111",customerResponse.getData().getPersonalProfile().getCellPhone());
    }

    @Test
    void updateCustomerDetails() {
        CustomerDataTest customerDataTest = new CustomerDataTest();
        CustomerResponseDTO customerResponse = customerDataTest.updateCustomerResponse();
        CustomerService mockCustomerService = mock(CustomerService.class);
        when(mockCustomerService.updateCustomerDetails(customerDataTest.updateCustomerRequest())).thenReturn(customerResponse.getData());

        assertEquals("Accept",customerResponse.getData().getStatus());
        assertEquals("89455",customerResponse.getData().getSalaryAccountNumber());
        assertEquals("122199983",customerResponse.getData().getAccountABANumber());
    }

    @Test
    void validateAccountRequest() {

        CustomerDataTest customerDataTest = new CustomerDataTest();
        CustomerResponseDTO customerResponse = customerDataTest.validateAccountResponse();
        
        CustomerService mockCustomerService = mock(CustomerService.class);
        when(mockCustomerService.validateAccountRequest(customerDataTest.validateAccountRequest())).thenReturn(customerResponse.getData());

        assertEquals("Accept",customerResponse.getData().getStatus());
        assertEquals("89455",customerResponse.getData().getSalaryAccountNumber());
        assertEquals("122199983",customerResponse.getData().getAccountABANumber());
    }
}