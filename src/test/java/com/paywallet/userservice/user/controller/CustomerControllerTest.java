package com.paywallet.userservice.user.controller;

import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.helper.CustomerDataTest;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.CustomerAccountResponseDTO;
import com.paywallet.userservice.user.model.CustomerResponseDTO;
import com.paywallet.userservice.user.model.UpdateCustomerRequestDTO;
import com.paywallet.userservice.user.model.ValidateAccountRequest;
import com.paywallet.userservice.user.services.CustomerService;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    public static final String GET_ACCOUNT_DETAILS_TEST = "/api/v1/customer/account/{mobileNo}";
    public static final String GET_CUSTOMER_BY_MOBILENO_TEST = "/api/v1/customer/{mobileNo}";
    public static final String GET_CUSTOMER_TEST = "/api/v1/customer/get/{customerId}";
    public static final String CREATE_CUSTOMER_TEST = "/api/v1/customer/create";
    public static final String VALIDATE_CUSTOMER_ACCOUNT_TEST = "/api/v1/customer/account/validate";
    public static final String  UPDATE_CUSTOMER_TEST = "/api/v1/customer/update";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;
    
    @Test
    void getAccountDetails() throws Exception {

        CustomerDataTest customerDataTest = new CustomerDataTest();
        CustomerAccountResponseDTO testAccDetails = customerDataTest.getAccDetails();

        String mobileNo="+919980024111";

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(GET_ACCOUNT_DETAILS_TEST,mobileNo)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        when(customerService.getAccountDetails(mobileNo)).thenReturn(testAccDetails.getData());
        when(customerService.prepareAccountDetailsResponseDTO(Mockito.any(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyString()))
        	.thenReturn(testAccDetails);
        
        mockMvc.perform(mockRequest).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.salaryAccountNumber").value(testAccDetails.getData().getSalaryAccountNumber()))
                .andExpect(jsonPath("$.data.abaOfSalaryAccount").value(testAccDetails.getData().getAbaOfSalaryAccount()));
    }

    @Test
    void createCustomer() throws Exception{

        CustomerDataTest customerDataTest = new CustomerDataTest();
        CreateCustomerRequest customerRequest = customerDataTest.createCustomerRequest();
        String apiKey = "123456";
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_CUSTOMER_TEST).header(REQUEST_ID, apiKey)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(customerRequest));

        CustomerResponseDTO customerResponse = customerDataTest.createCustomerResponse();
        CustomerDetails customerDetails =  customerResponse.getData();
        when(customerService.createCustomer(any(), Mockito.anyString())).thenReturn(customerDetails);
        when(customerService.prepareResponseDTO(Mockito.any(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyString())).thenReturn(customerResponse);
        
        mockMvc.perform(mockRequest).andDo(print()).andExpect(status().isOk());
                //.andExpect(jsonPath("$.data.customerId").value(customerResponse.getData().getCustomerId()))
                //.andExpect(jsonPath("$.data.personalProfile.mobileNo").value(customerResponse.getData().getPersonalProfile().getMobileNo()));
    }
    
    @Test
    void getCustomer() throws Exception{

        CustomerDataTest customerDataTest = new CustomerDataTest();
        String customerId = "61822f23019cba309dd5b070";
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(GET_CUSTOMER_TEST, customerId)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        CustomerResponseDTO customerResponse = customerDataTest.createCustomerResponse();
        CustomerDetails customerDetails =  customerResponse.getData();
        when(customerService.getCustomer(customerId)).thenReturn(customerDetails);
        when(customerService.prepareResponseDTO(Mockito.any(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyString())).thenReturn(customerResponse);
        
        mockMvc.perform(mockRequest).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerId").value(customerResponse.getData().getCustomerId()))
                .andExpect(jsonPath("$.data.personalProfile.mobileNo").value(customerResponse.getData().getPersonalProfile().getMobileNo()));
    }
    
    @Test
    void getCustomerByMobileNo() throws Exception{

        CustomerDataTest customerDataTest = new CustomerDataTest();
        String mobileNo="+919980024111";
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(GET_CUSTOMER_BY_MOBILENO_TEST, mobileNo)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        CustomerResponseDTO customerResponse = customerDataTest.createCustomerResponse();
        CustomerDetails customerDetails =  customerResponse.getData();
        when(customerService.getCustomerByMobileNo(mobileNo)).thenReturn(customerDetails);
        when(customerService.prepareResponseDTO(Mockito.any(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyString())).thenReturn(customerResponse);
        
        mockMvc.perform(mockRequest).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerId").value(customerResponse.getData().getCustomerId()))
                .andExpect(jsonPath("$.data.personalProfile.mobileNo").value(customerResponse.getData().getPersonalProfile().getMobileNo()));
    }

    @Test
    void validateAccount() throws Exception{

        CustomerDataTest customerDataTest = new CustomerDataTest();
        ValidateAccountRequest validateAccountRequest = customerDataTest.validateAccountRequest();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(VALIDATE_CUSTOMER_ACCOUNT_TEST)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(validateAccountRequest));

        CustomerResponseDTO validateAccountResponse = customerDataTest.validateAccountResponse();
        when(customerService.validateAccountRequest(any())).thenReturn(validateAccountResponse.getData());
        when(customerService.prepareResponseDTO(Mockito.any(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyString())).thenReturn(validateAccountResponse);

        mockMvc.perform(mockRequest).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(validateAccountResponse.getData().getStatus()))
                .andExpect(jsonPath("$.data.salaryAccountNumber").value(validateAccountResponse.getData().getSalaryAccountNumber()))
                .andExpect(jsonPath("$.data.abaOfSalaryAccount").value(validateAccountResponse.getData().getAbaOfSalaryAccount()));
    }

    @Test
    void updateCustomer() throws Exception{
        CustomerDataTest customerDataTest = new CustomerDataTest();
        UpdateCustomerRequestDTO updateCustomerRequestTest = customerDataTest.updateCustomerRequest();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(UPDATE_CUSTOMER_TEST)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updateCustomerRequestTest));

        CustomerResponseDTO updateCustomer = customerDataTest.updateCustomerResponse();
        when(customerService.updateCustomerDetails(any())).thenReturn(updateCustomer.getData());
        when(customerService.prepareResponseDTO(Mockito.any(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyString())).thenReturn(updateCustomer);
        
        mockMvc.perform(mockRequest).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(updateCustomer.getData().getStatus()))
                .andExpect(jsonPath("$.data.salaryAccountNumber").value(updateCustomer.getData().getSalaryAccountNumber()))
                .andExpect(jsonPath("$.data.abaOfSalaryAccount").value(updateCustomer.getData().getAbaOfSalaryAccount()));
    }

     @Test
    void getAccountDetailsOfnotExistingCustomer() throws Exception {

        String mobileNo="919980024111";//customer do not exist with this mobile no

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(GET_ACCOUNT_DETAILS_TEST,mobileNo)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        when(customerService.getAccountDetails(mobileNo)).thenThrow(new CustomerNotFoundException("Customer not present with the mobileNo: 919980024111 to fetch account details"));
        mockMvc.perform(mockRequest).andDo(print()).andExpect(status().isBadRequest());

    }

     @Test
    void updateNonexistingCustomer() throws Exception{
        CustomerDataTest customerDataTest = new CustomerDataTest();
        UpdateCustomerRequestDTO updateCustomerRequestTest = customerDataTest.updateCustomerRequest();
        updateCustomerRequestTest.setMobileNo("+919980025222"); //customer do not exist with this mobile no

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(UPDATE_CUSTOMER_TEST)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updateCustomerRequestTest));

        when(customerService.updateCustomerDetails(any())).thenThrow(new CustomerNotFoundException("Customer do not exists with the mobileNo: +919980025222 to update"));

        mockMvc.perform(mockRequest).andDo(print()).andExpect(status().isBadRequest());
    }
}