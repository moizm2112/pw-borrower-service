package com.paywallet.userservice.user.controller;

import static com.paywallet.userservice.user.constant.AppConstants.BASE_PATH;
import static com.paywallet.userservice.user.constant.AppConstants.CREATE_CUSTOMER;
import static com.paywallet.userservice.user.constant.AppConstants.GET_ACCOUNT_DETAILS;
import static com.paywallet.userservice.user.constant.AppConstants.GET_CUSTOMER;
import static com.paywallet.userservice.user.constant.AppConstants.GET_CUSTOMER_BY_MOBILENO;
import static com.paywallet.userservice.user.constant.AppConstants.UPDATE_CUSTOMER;
import static com.paywallet.userservice.user.constant.AppConstants.UPDATE_CUSTOMER_EMAILID;
import static com.paywallet.userservice.user.constant.AppConstants.UPDATE_CUSTOMER_MOBILENO;
import static com.paywallet.userservice.user.constant.AppConstants.VALIDATE_CUSTOMER_ACCOUNT;
import static com.paywallet.userservice.user.constant.AppConstants.REQUEST_ID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.paywallet.userservice.user.util.CustomerServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.CommonEnum;
import com.paywallet.userservice.user.exception.CreateCustomerException;
import com.paywallet.userservice.user.exception.CustomerAccountException;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.model.AccountDetails;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.CustomerAccountResponseDTO;
import com.paywallet.userservice.user.model.CustomerResponseDTO;
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
        CustomerDetails customerDetails = customerService.createCustomer(customer, requestId);
        return customerService.prepareResponse(customerDetails, CommonEnum.CUSTOMER_CREATED_SUCCESS_MSG.getMessage(),
				HttpStatus.CREATED.value(), request.getRequestURI());		
    }
    
    /**
     * This method returns customer details by mobile number
     * @param mobileNo
     * @param request
     * @return
     * @throws CustomerNotFoundException
     */
    @GetMapping(GET_CUSTOMER_BY_MOBILENO)
    public CustomerResponseDTO getCustomerByMobile(@PathVariable (required = true) String mobileNo, HttpServletRequest request)
    		throws CustomerNotFoundException {
        log.debug("Get Customer details for the given mobileNo: " + CustomerServiceUtil.mask(mobileNo));
        CustomerDetails customerDetails = customerService.getCustomerByMobileNo(mobileNo);
        return customerService.prepareResponseDTO(customerDetails, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());
    }
    
    /**
     * This method returns customer details by mobile number
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
     * @param mobileNo
     * @param request
     * @return
     * @throws CustomerAccountException
     * @throws CustomerNotFoundException
     */
    @GetMapping(GET_ACCOUNT_DETAILS)
    public CustomerAccountResponseDTO getAccountDetails(@PathVariable (required = true) String mobileNo, HttpServletRequest request)
    		throws CustomerAccountException, CustomerNotFoundException{
        log.debug("Get Account details for the customer with mobileNo: " + CustomerServiceUtil.mask(mobileNo));
        AccountDetails accountDetails = customerService.getAccountDetails(mobileNo);
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
     * Method updates the customer mobileNo
     * @param UpdateCustomerMobileNoDTO
     * @param request
     * @return
     * @throws CustomerNotFoundException
     */
   /* @PutMapping(UPDATE_CUSTOMER_MOBILENO)
    public CustomerResponseDTO updateCustomerMobileNo(@Valid @RequestBody UpdateCustomerMobileNoDTO updateCustomerDetailsDTO, @RequestHeader(REQUEST_ID) String requestId,
    		 HttpServletRequest request)
    		throws CustomerNotFoundException {
        log.debug("Inside update customer mobileNo " + updateCustomerDetailsDTO);
        CustomerDetails customerDetails = customerService.updateCustomerMobileNo(updateCustomerDetailsDTO, requestId);
        return customerService.prepareResponseDTO(customerDetails, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());
    }*/
    
    /**
     * Method updates the customer mobileNo
     * @param UpdateCustomerMobileNoDTO
     * @param request
     * @return
     * @throws CustomerNotFoundException
     */
    /*@PutMapping(UPDATE_CUSTOMER_EMAILID)
    public CustomerResponseDTO updateCustomerEmailId(@Valid @RequestBody UpdateCustomerEmailIdDTO updateCustomerEmailIdDTO, @RequestHeader(REQUEST_ID) String requestId,
    		 HttpServletRequest request)
    		throws CustomerNotFoundException {
        log.debug("Inside update customer emailId " + updateCustomerEmailIdDTO);
        CustomerDetails customerDetails = customerService.updateCustomerEmailId(updateCustomerEmailIdDTO, requestId);
        return customerService.prepareResponseDTO(customerDetails, CommonEnum.SUCCESS_STATUS_MSG.getMessage(),
        		HttpStatus.OK.value(), request.getRequestURI());
    }*/

}
