package com.paywallet.userservice.user.services.wrapperapi.employement;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationResponseDTO;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.CustomerServiceHelper;
import com.paywallet.userservice.user.services.allowretry.AllowRetryAPIUtil;
import com.paywallet.userservice.user.util.KafkaPublisherUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmploymentRetryWrapperAPIService {

    @Autowired
    RequestIdUtil requestIdUtil;

    @Autowired
    AllowRetryAPIUtil allowRetryAPIUtil;
    
    @Autowired
    CustomerService customerService;
    
    @Autowired
    CustomerServiceHelper customerServiceHelper;
    
    @Autowired
    KafkaPublisherUtil kafkaPublisherUtil;
    
    @Value("${identifyProviderService.eureka.uri}")
   	private String identifyProviderServiceUri;
       

    /**
     * checking for allow retry status, if retry is allowed, then re-initiating employment verification
     **/
    public EmploymentResponseInfo retryEmploymentVerification(String requestId, EmploymentVerificationRequestDTO empVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails= requestIdUtil.fetchRequestIdDetails(requestId);
        //Check whether retry can be initiated.
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails);
        // initiate retry code logic -> need to add
        initiateEmploymentVerification(requestIdDetails, requestId , empVerificationRequestDTO);
        return this.prepareEmploymentResponseInfo(empVerificationRequestDTO);

    }

    public void initiateEmploymentVerification(RequestIdDetails requestIdDetails, String requestId, EmploymentVerificationRequestDTO empVerificationRequestDTO) {
    	log.info(" Inside initiateEmploymentVerification, with RequestDetails as ::" , requestIdDetails);
    	CustomerDetails customer = Optional.ofNullable(customerService.getCustomer(requestIdDetails.getUserId()))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Customer not found"));
    	log.info(" Received the CustomerDetails ::" , customer);
    	requestIdDetails = validateInput( customer, requestId,  requestIdDetails, empVerificationRequestDTO) ;
    	kafkaPublisherUtil.publishLinkServiceInfo(requestIdDetails,customer, FlowTypeEnum.EMPLOYMENT_VERIFICATION);
    }

    public EmploymentResponseInfo prepareEmploymentResponseInfo(EmploymentVerificationRequestDTO empVerificationRequestDTO){
       // Need to change the reading fields from request
        return EmploymentResponseInfo.builder()
                .employer(empVerificationRequestDTO.getEmployerId())
                .emailId(empVerificationRequestDTO.getEmailId())
                .mobileNo(empVerificationRequestDTO.getMobileNo())
                .build();
    }

    public EmploymentVerificationResponseDTO prepareResponseDTO(EmploymentResponseInfo employmentResponseInfo, String code, int value, String requestURI, String message) {
        return EmploymentVerificationResponseDTO.builder()
                .data(employmentResponseInfo)
                .message(message)
                .path(requestURI)
                .timeStamp(new Date())
                .status(code)
                .build();
    }
    
    public RequestIdDetails validateInput(CustomerDetails customer,String requestId, RequestIdDetails requestIdDetails,
    		EmploymentVerificationRequestDTO empVerificationRequestDTO) {
    	log.info("Inside validateInput");
    	log.info(customer.getPersonalProfile().getMobileNo());
    	log.info(customer.getPersonalProfile().getEmailId());
    	log.info(empVerificationRequestDTO.getMobileNo());
    	log.info(empVerificationRequestDTO.getEmailId());
    	//Check if the employer Id in the Request Table has been changed with new employerId in the Retry Request. If yes, call the select employer
    	if(! requestIdDetails.getEmployerPWId().equals(empVerificationRequestDTO.getEmployerId())) {
    		log.info("Employer Changed. Updating the new employer");
    		customerServiceHelper.getEmployerDetailsBasedOnEmployerId(empVerificationRequestDTO.getEmployerId(),requestId);
    		requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
    	}
    	if(! customer.getPersonalProfile().getMobileNo().equals(empVerificationRequestDTO.getMobileNo()) ||
    			!customer.getPersonalProfile().getEmailId().equals(empVerificationRequestDTO.getEmailId())) {
    		throw new  GeneralCustomException("ERROR", "The Customer Email or Cell Number cannot be changed");
    	} 
    	return requestIdDetails;
    	
    	
    }
}
