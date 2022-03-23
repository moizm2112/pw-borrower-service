package com.paywallet.userservice.user.services.wrapperapi.income;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.LenderConfigInfo;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeVerificationResponseDTO;
import com.paywallet.userservice.user.services.CustomerFieldValidator;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.CustomerServiceHelper;
import com.paywallet.userservice.user.services.CustomerWrapperAPIService;
import com.paywallet.userservice.user.services.allowretry.AllowRetryAPIUtil;
import com.paywallet.userservice.user.util.KafkaPublisherUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IncomeRetryWrapperAPIService {

    @Autowired
    RequestIdUtil requestIdUtil;

    @Autowired
    AllowRetryAPIUtil allowRetryAPIUtil;
    
    @Autowired
    CustomerServiceHelper customerServiceHelper;
    
    @Autowired
    KafkaPublisherUtil kafkaPublisherUtil;

    @Autowired
    CustomerService customerService;
    
    @Autowired
    CustomerWrapperAPIService customerWrapperService;
    
    @Autowired
    CustomerFieldValidator customerFieldValidator;
    
    @Autowired
    RestTemplate restTemplate;
    

    /**
     * checking for allow retry status, if retry is allowed, then re-initiating income verification
     **/
    public IncomeResponseInfo retryIncomeVerification(String requestId, IncomeVerificationRequestWrapperModel incomeVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails);
        // initiate retry code logic -> need to add
        CustomerDetails customer = initiateIncomeVerification(requestIdDetails, requestId, incomeVerificationRequestDTO);
        return this.prepareIncomeResponseInfo(customer, incomeVerificationRequestDTO);

    }

    public CustomerDetails initiateIncomeVerification(RequestIdDetails requestIdDetails, String requestId, IncomeVerificationRequestWrapperModel incomeVerificationRequestDTO) throws RetryException {
    	log.info(" Inside initiateIncomeVerification, with RequestDetails as ::" , requestIdDetails);
    	CustomerDetails customer = Optional.ofNullable(customerService.getCustomer(requestIdDetails.getUserId()))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Customer not found"));
    	log.info(" Received the CustomerDetails ::" , customer);
    	requestIdDetails = validateInput( customer, requestId,  requestIdDetails,  incomeVerificationRequestDTO) ;
    	kafkaPublisherUtil.publishLinkServiceInfo(requestIdDetails,customer, FlowTypeEnum.INCOME_VERIFICATION);
    	return customer;
    }

    public IncomeResponseInfo prepareIncomeResponseInfo(CustomerDetails customer, IncomeVerificationRequestWrapperModel incomeVerificationRequestDTO) {
        // Need to change the reading fields from request
        return IncomeResponseInfo.builder()
                .employer(incomeVerificationRequestDTO.getEmployerId())
                .emailId(incomeVerificationRequestDTO.getEmailId())
                .cellPhone(customer.getPersonalProfile().getCellPhone())
                .build();
    }

    public IncomeVerificationResponseDTO prepareResponseDTO(IncomeResponseInfo incomeResponseInfo, String status, String requestURI, String message) {
        return IncomeVerificationResponseDTO.builder()
                .data(incomeResponseInfo)
                .message(message)
                .path(requestURI)
                .timeStamp(new Date())
                .status(status)
                .build();
    }
    
    public RequestIdDetails validateInput(CustomerDetails customer,String requestId, RequestIdDetails requestIdDetails,
    		IncomeVerificationRequestWrapperModel incomeVerificationRequestDTO) throws RetryException {
    	log.info("Inside validateInput");
    	log.info(customer.getPersonalProfile().getCellPhone());
    	log.info(customer.getPersonalProfile().getEmailId());
    	log.info(incomeVerificationRequestDTO.getCellPhone());
    	log.info(incomeVerificationRequestDTO.getEmailId());
    	
    	customerWrapperService.validateMobileFromRequest(incomeVerificationRequestDTO.getCellPhone(),"Income");
    	//Check if the employer Id in the Request Table has been changed with new employerId in the Retry Request. If yes, call the select employer
    	if(! requestIdDetails.getEmployerPWId().equals(incomeVerificationRequestDTO.getEmployerId())) {
    		log.info("Employer Changed. Updating the new employer");
    		customerServiceHelper.getEmployerDetailsBasedOnEmployerId(incomeVerificationRequestDTO.getEmployerId(),requestId);
    		requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
    	}

        if((!customer.getPersonalProfile().getCellPhone().contains(incomeVerificationRequestDTO.getCellPhone()) )&&
                (!customer.getPersonalProfile().getEmailId().equals(incomeVerificationRequestDTO.getEmailId()))){
            throw new RetryException("Both Email ID and Mobile No. does not match with the request ID.");
        }

        if(! customer.getPersonalProfile().getCellPhone().contains(incomeVerificationRequestDTO.getCellPhone())) {
            throw new RetryException("Mobile No. does not match with the request ID.");
        }

        if(	!customer.getPersonalProfile().getEmailId().equals(incomeVerificationRequestDTO.getEmailId())) {
            throw new RetryException("Email Id does not match with the request ID.");
        }



    	return requestIdDetails;
    }
}
