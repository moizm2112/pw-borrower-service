package com.paywallet.userservice.user.util;

import com.paywallet.userservice.user.constant.AppConstants;
import com.paywallet.userservice.user.entities.OfferPayAllocationRequest;
import com.paywallet.userservice.user.entities.OfferPayAllocationResponse;
import com.paywallet.userservice.user.enums.StateStatus;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.StateControllerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class LinkServiceUtil {

    @Autowired
    RestHelper restHelper;

    @Value("${admin.service.url}")
    private String adminServiceUrl;

    @Value("${allocation.service.url}")
    private String allocationServiceUrl;

    public StateControllerInfo getStateInfo(String requestId, String lenderName) {

        String endUrl = UriComponentsBuilder.fromHttpUrl(adminServiceUrl).
                queryParam(AppConstants.LENDER_NAME).encode().toUriString();
        HttpHeaders httpHeaders = prepareHeader();
        httpHeaders.set(AppConstants.REQUEST_ID, requestId);
        return restHelper.get(endUrl, httpHeaders, StateControllerInfo.class);

    }

    public boolean checkStateInfo(StateControllerInfo stateControllerInfo) {

        StateStatus employmentStatus = stateControllerInfo.getPublishEmploymentInfo();
        StateStatus incomeStatus = stateControllerInfo.getPublishIncomeInfo();
        StateStatus identityStatus = stateControllerInfo.getPublishIdentityInfo();
        StateStatus affordStatus = stateControllerInfo.getValidateAffordabilityCheck();
        StateStatus depositStatus = stateControllerInfo.getInvokeAndPublishDepositAllocation();
        if (depositStatus.equals(StateStatus.YES) &&
                (employmentStatus.equals(StateStatus.NO) && (incomeStatus.equals(StateStatus.NO)
                        && identityStatus.equals(StateStatus.NO) && affordStatus.equals(StateStatus.NO)))) {
            return true;
        }
        return false;

    }

    public OfferPayAllocationRequest prepareCheckAffordabilityRequest(CreateCustomerRequest customer) {

        return OfferPayAllocationRequest.builder()
                .installmentAmount(String.valueOf(customer.getInstallmentAmount()))
                .firstRepaymentDate(customer.getFirstDateOfPayment())
                .numberOfInstallment(customer.getTotalNoOfRepayment())
                .build();
    }

    public OfferPayAllocationResponse postCheckAffordabilityRequest(OfferPayAllocationRequest offerPayAllocationRequest,
                                                                    String requestId) {
        HttpHeaders httpHeaders = prepareHeader();
        httpHeaders.set(AppConstants.REQUEST_ID, requestId);
        HttpEntity httpEntity = new HttpEntity<>(offerPayAllocationRequest, httpHeaders);
        return restHelper.post(allocationServiceUrl, httpEntity, OfferPayAllocationResponse.class);
    }

    public HttpHeaders prepareHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }


}
