package com.paywallet.userservice.user.services.wrapperapi.income;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.enums.FlowTypeEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.exception.RequestIdNotFoundException;
import com.paywallet.userservice.user.exception.RetryException;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.employement.EmploymentVerificationResponseDTO;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeResponseInfo;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeVerificationRequestDTO;
import com.paywallet.userservice.user.model.wrapperAPI.income.IncomeVerificationResponseDTO;
import com.paywallet.userservice.user.services.CustomerService;
import com.paywallet.userservice.user.services.CustomerServiceHelper;
import com.paywallet.userservice.user.services.allowretry.AllowRetryAPIUtil;
import com.paywallet.userservice.user.util.KafkaPublisherUtil;
import com.paywallet.userservice.user.util.RequestIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Date;
import java.util.Optional;

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
    public IncomeResponseInfo retryIncomeVerification(String requestId, IncomeVerificationRequestDTO incomeVerificationRequestDTO) throws RequestIdNotFoundException, ResourceAccessException, GeneralCustomException, RetryException {

        RequestIdDetails requestIdDetails = requestIdUtil.fetchRequestIdDetails(requestId);
        allowRetryAPIUtil.checkForRetryStatus(requestIdDetails);
        // initiate retry code logic -> need to add
        initiateIncomeVerification(requestIdDetails);
        return this.prepareIncomeResponseInfo(incomeVerificationRequestDTO);

    }

    public void initiateIncomeVerification(RequestIdDetails requestIdDetails) {
    	log.info(" Inside initiateIncomeVerification, with RequestDetails as ::" , requestIdDetails);
    	CustomerDetails customer = Optional.ofNullable(customerService.getCustomer(requestIdDetails.getUserId()))
		   		.orElseThrow(() -> new RequestIdNotFoundException("Customer not found"));
    	log.info(" Received the CustomerDetails ::" , customer);
    	kafkaPublisherUtil.publishLinkServiceInfo(requestIdDetails,customer, FlowTypeEnum.EMPLOYMENT_VERIFICATION);
    }

    public IncomeResponseInfo prepareIncomeResponseInfo(IncomeVerificationRequestDTO incomeVerificationRequestDTO) {
        // Need to change the reading fields from request
        return IncomeResponseInfo.builder()
                .employer(incomeVerificationRequestDTO.getEmployerId())
                .emailId(incomeVerificationRequestDTO.getEmailId())
                .mobileNo(incomeVerificationRequestDTO.getMobileNo())
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
}
