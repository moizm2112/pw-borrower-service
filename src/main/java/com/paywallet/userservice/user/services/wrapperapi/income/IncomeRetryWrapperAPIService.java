package com.paywallet.userservice.user.services.wrapperapi.income;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeVerificationResponseDTO;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.CustomerServiceHelper;
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
    

    /**
     * checking for allow retry status, if retry is allowed, then re-initiating income verification
     **/
    public IncomeResponseInfo retryIncomeVerification(String requestId, IncomeVerificationRequestWrapperModel incomeVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails);
        // initiate retry code logic -> need to add
        initiateIncomeVerification(requestIdDetails, requestId, incomeVerificationRequestDTO);
        return this.prepareIncomeResponseInfo(incomeVerificationRequestDTO);

    }

    public void initiateIncomeVerification(RequestIdDetails requestIdDetails, String requestId, IncomeVerificationRequestWrapperModel incomeVerificationRequestDTO) {
    	log.info(" Inside initiateIncomeVerification, with RequestDetails as ::" , requestIdDetails);
    	CustomerDetails customer = Optional.ofNullable(customerService.getCustomer(requestIdDetails.getUserId()))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Customer not found"));
    	log.info(" Received the CustomerDetails ::" , customer);
    	requestIdDetails = validateInput( customer, requestId,  requestIdDetails,  incomeVerificationRequestDTO) ;
    	kafkaPublisherUtil.publishLinkServiceInfo(requestIdDetails,customer, FlowTypeEnum.INCOME_VERIFICATION);
    }

    public IncomeResponseInfo prepareIncomeResponseInfo(IncomeVerificationRequestWrapperModel incomeVerificationRequestDTO) {
        // Need to change the reading fields from request
        return IncomeResponseInfo.builder()
                .employer(incomeVerificationRequestDTO.getEmployerId())
                .emailId(incomeVerificationRequestDTO.getEmailId())
                .cellPhone(incomeVerificationRequestDTO.getCellPhone())
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
    		IncomeVerificationRequestWrapperModel incomeVerificationRequestDTO) {
    	log.info("Inside validateInput");
    	log.info(customer.getPersonalProfile().getCellPhone());
    	log.info(customer.getPersonalProfile().getEmailId());
    	log.info(incomeVerificationRequestDTO.getCellPhone());
    	log.info(incomeVerificationRequestDTO.getEmailId());

    	//Check if the employer Id in the Request Table has been changed with new employerId in the Retry Request. If yes, call the select employer
    	if(! requestIdDetails.getEmployerPWId().equals(incomeVerificationRequestDTO.getEmployerId())) {
    		log.info("Employer Changed. Updating the new employer");
    		customerServiceHelper.getEmployerDetailsBasedOnEmployerId(incomeVerificationRequestDTO.getEmployerId(),requestId);
    		requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
    	}

        if(	!customer.getPersonalProfile().getEmailId().equals(incomeVerificationRequestDTO.getEmailId())) {
            throw new  GeneralCustomException("ERROR", "Email Id does not match with the request ID.");
        }

        if(! customer.getPersonalProfile().getCellPhone().equals(incomeVerificationRequestDTO.getCellPhone())) {
            throw new  GeneralCustomException("ERROR", "Mobile No. does not match with the request ID.");
        }

    	return requestIdDetails;
    }
}
