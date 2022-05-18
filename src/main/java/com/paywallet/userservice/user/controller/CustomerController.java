package com.paywallet.userservice.user.controller;

import static com.paywallet.userservice.user.constant.AppConstants.BASE_PATH;
import static com.paywallet.userservice.user.constant.AppConstants.CREATE_CUSTOMER;
import static com.paywallet.userservice.user.constant.AppConstants.GET_ACCOUNT_DETAILS;
import static com.paywallet.userservice.user.constant.AppConstants.GET_CUSTOMER;
import static com.paywallet.userservice.user.constant.AppConstants.GET_CUSTOMER_BY_CELLPHONE;
import static com.paywallet.userservice.user.constant.AppConstants.UPDATE_CUSTOMER;
import static com.paywallet.userservice.user.constant.AppConstants.UPDATE_CUSTOMER_EMAILID;
import static com.paywallet.userservice.user.constant.AppConstants.UPDATE_CUSTOMER_CELLPHONE;
import static com.paywallet.userservice.user.constant.AppConstants.VALIDATE_CUSTOMER_ACCOUNT;

import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;
import static com.paywallet.userservice.user.constant.AppConstants.ADD_REQUIRED_FIELDS;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paywallet.userservice.user.constant.URIConstants;
import com.paywallet.userservice.user.dto.PayrollProviderDetailsDTO;
import com.paywallet.userservice.user.entities.CustomerProvidedDetails;
import com.paywallet.userservice.user.util.CustomerServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.CustomerAccountException;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.model.AccountDetails;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.CustomerAccountResponseDTO;
import com.paywallet.userservice.user.model.CustomerRequestFields;
import com.paywallet.userservice.user.model.CustomerResponseDTO;
import com.paywallet.userservice.user.model.UpdateCustomerDetailsResponseDTO;
import com.paywallet.userservice.user.model.UpdateCustomerEmailIdDTO;
import com.paywallet.userservice.user.model.UpdateCustomerMobileNoDTO;
import com.paywallet.userservice.user.model.UpdateCustomerRequestDTO;
import com.paywallet.userservice.user.model.ValidateAccountRequest;
import com.paywallet.userservice.user.services.CustomerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(BASE_PATH)
public class CustomerController {

    @Autowired
    CustomerService customerService;

    /**
     * Method creates new customer with fineract virtual account
     * @param customer
     * @param request
     * @return
     * @throws MethodArgumentNotValidException
     * @throws CreateCustomerException
     * @throws RequestIdNotFoundException
     */
    @PostMapping(CREATE_CUSTOMER)
    public ResponseEntity<Object> createCustomer(@Valid @RequestBody CreateCustomerRequest customer, @RequestHeader(REQUEST_ID) String requestId,
    		HttpServletRequest request)
    		throws MethodArgumentNotValidException, CreateCustomerException, RequestIdNotFoundException {
        log.debug("Inside Create Customer controller " + customer);
        CustomerDetails customerDetails = customerService.createCustomer(customer, requestId, null, FlowTypeEnum.GENERAL);
        if(customerDetails.isExistingCustomer())
        	return customerService.prepareResponse(customerDetails, CommonEnum.CUSTOMER_EXIST_SUCCESS_MSG.getMessage(),
        			HttpStatus.OK.value(), request.getRequestURI());
        return customerService.prepareResponse(customerDetails, CommonEnum.CUSTOMER_CREATED_SUCCESS_MSG.getMessage(),
        		HttpStatus.CREATED.value(), request.getRequestURI());		
    }
    
    /**
     * This method returns customer details by CellPhone Number
     * @param cellPhone
     * @param request
     * @return
     * @throws CustomerNotFoundException
     */
    @GetMapping(GET_CUSTOMER_BY_CELLPHONE)
    public CustomerResponseDTO getCustomerByMobile(@PathVariable (required = true) String cellPhone, HttpServletRequest request)
    		throws CustomerNotFoundException {
        log.debug("Get Customer details for the given cellPhone: " + CustomerServiceUtil.mask(cellPhone));
        CustomerDetails customerDetails = customerService.getCustomerByMobileNo(cellPhone);
        return customerService.prepareResponseDTO(customerDetails, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());
    }
    
    /**
     * This method returns customer details by CellPhone Number
     * @param customerId
     * @param request
     * @return
     * @throws CustomerNotFoundException
     */
    @GetMapping(GET_CUSTOMER)
    public CustomerResponseDTO getCustomer(@PathVariable (required = true) String customerId, HttpServletRequest request)
    		throws CustomerNotFoundException {
        log.debug("Get Customer details for the given customerId: " + customerId);
        CustomerDetails customerDetails = customerService.getCustomer(customerId);
        return customerService.prepareResponseDTO(customerDetails, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());
    }
    
    /**
     * @param cellPhone
     * @param request
     * @return
     * @throws CustomerAccountException
     * @throws CustomerNotFoundException
     */
    @GetMapping(GET_ACCOUNT_DETAILS)
    public CustomerAccountResponseDTO getAccountDetails(@PathVariable (required = true) String cellPhone, HttpServletRequest request)
    		throws CustomerAccountException, CustomerNotFoundException{
        log.debug("Get Account details for the customer with cellPhone: " + CustomerServiceUtil.mask(cellPhone));
        AccountDetails accountDetails = customerService.getAccountDetails(cellPhone);
        return customerService.prepareAccountDetailsResponseDTO(accountDetails, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());

    }

    /**
     * Method validates the customer account information provided again lyons API
     * @param validateAccountRequest
     * @param request
     * @return
     * @throws GeneralCustomException
     * @throws CustomerNotFoundException
     */
    @PostMapping(VALIDATE_CUSTOMER_ACCOUNT)
    public CustomerResponseDTO validateAccount(@Valid @RequestBody ValidateAccountRequest validateAccountRequest,
    		HttpServletRequest request) throws GeneralCustomException, CustomerNotFoundException{
        log.debug("Inside Validate Customer Account details are : " + validateAccountRequest);
        CustomerDetails customerDetails = customerService.validateAccountRequest(validateAccountRequest);
        return customerService.prepareResponseDTO(customerDetails, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());

    }

    /**
     * Method updates the customer salary profile
     * @param updateCustomerRequest
     * @param request
     * @return
     * @throws CustomerNotFoundException
     */
    @PutMapping(UPDATE_CUSTOMER)
    public CustomerResponseDTO updateCustomer(@Valid @RequestBody UpdateCustomerRequestDTO updateCustomerRequest, HttpServletRequest request)
    		throws CustomerNotFoundException {
        log.debug("Inside update customer details " + updateCustomerRequest);
        CustomerDetails customerDetails = customerService.updateCustomerDetails(updateCustomerRequest);
        return customerService.prepareResponseDTO(customerDetails, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());
    }
    
    /**
     * Method updates the customer cellPhone
     * @param updateCustomerDetailsDTO
     * @param request
     * @return
     * @throws CustomerNotFoundException
     */
    @PutMapping(UPDATE_CUSTOMER_CELLPHONE)
    public ResponseEntity<Object> updateCustomerMobileNo(@Valid @RequestBody UpdateCustomerMobileNoDTO updateCustomerDetailsDTO, @RequestHeader(REQUEST_ID) String requestId,
    		 HttpServletRequest request)
    		throws CustomerNotFoundException {
        log.debug("Inside update customer cellPhone " + updateCustomerDetailsDTO);
        UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponseDTO = customerService.updateCustomerMobileNo(updateCustomerDetailsDTO, requestId);
        return customerService.prepareUpdateResponse(updateCustomerDetailsResponseDTO, CommonEnum.UPDATE_CELLPHONE_SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());
    }
    
    /**
     * Method updates the customer cellPhone
     * @param updateCustomerEmailIdDTO
     * @param request
     * @return
     * @throws CustomerNotFoundException
     */
    @PutMapping(UPDATE_CUSTOMER_EMAILID)
    public ResponseEntity<Object> updateCustomerEmailId(@Valid @RequestBody UpdateCustomerEmailIdDTO updateCustomerEmailIdDTO, @RequestHeader(REQUEST_ID) String requestId,
    		 HttpServletRequest request)
    		throws CustomerNotFoundException {
        log.debug("Inside update customer emailId " + updateCustomerEmailIdDTO);
        UpdateCustomerDetailsResponseDTO updateCustomerDetailsResponseDTO = customerService.updateCustomerEmailId(updateCustomerEmailIdDTO, requestId);
        return customerService.prepareUpdateResponse(updateCustomerDetailsResponseDTO, CommonEnum.UPDATE_EMAILID_SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());
    }
    
    @PostMapping(ADD_REQUIRED_FIELDS)
    public ResponseEntity<Object> addCustomerRequiredFields(@Valid @RequestBody CustomerRequestFields customerRequestFields,
    		HttpServletRequest request)
    		throws MethodArgumentNotValidException, CreateCustomerException, RequestIdNotFoundException {
        log.debug("Inside addCustomerRequiredFields -  Customer controller " + customerRequestFields);
        boolean isSuccess = customerService.addCustomerRequiredFields(customerRequestFields);
        if(isSuccess)
        	return ResponseEntity.status(HttpStatus.OK)
                .body("Success");
        else
        	return ResponseEntity.status(HttpStatus.OK)
                    .body("Failed");		
    }

    @GetMapping(URIConstants.GET_CUSTOMER_PROVIDED_DETAILS)
    public ResponseEntity getCustomerProviderDetails(@PathVariable("requestId") String requestId, HttpServletRequest request) {
        CustomerProvidedDetails customerProvidedDetails = customerService.getCustomerProvidedDetails(requestId);
        return customerService.prepareResponse(customerProvidedDetails,request.getRequestURI());
    }

    @PatchMapping(URIConstants.PATCH_PAYROLL_PROFILE)
    public ResponseEntity updatePayRollData(@PathVariable("customerId") String customerId, @RequestBody PayrollProviderDetailsDTO payrollProviderDetailsDTO, HttpServletRequest request) throws JsonProcessingException {
        String res = customerService.updatePayrollProfileDetails(customerId, payrollProviderDetailsDTO);
        return customerService.prepareResponse(res,request.getRequestURI());
    }

    @GetMapping(URIConstants.GET_PAYROLL_PROFILE)
    public ResponseEntity getPayrollProviderDetails(@PathVariable("customerId") String customerId, HttpServletRequest request) throws JsonProcessingException {
        PayrollProviderDetailsDTO payrollProviderDetailsDTO = customerService.getPayrollProfileDetails(customerId);
        return customerService.prepareResponse(payrollProviderDetailsDTO,request.getRequestURI());
    }

}
