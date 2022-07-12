package com.paywallet.userservice.user.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paywallet.userservice.user.enums.ServicesSelectedEnum;
import com.paywallet.userservice.user.exception.GeneralCustomException;
import com.paywallet.userservice.user.model.LenderConfigInfo;
import com.paywallet.userservice.user.model.RequestIdDetails;
import com.paywallet.userservice.user.model.SdkCreateCustomerRequest;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationRequestWrapperModel;

import io.sentry.Sentry;

@Service
public class SdkCustomerServiceHelper {

	@Autowired
	CustomerWrapperAPIService customerWrapperAPIService;
	
	private static final String EMPLOYMENT_VERIFICATION_REQUEST = "EMPLOYMENT VERIFICATION REQUEST IS MISSING";
	private static final String INCOME_VERIFICATION_REQUEST = "INCOME VERIFICATION REQUEST IS MISSING";
	private static final String DEPOSIT_VERIFICATION_REQUEST = "DEPOSIT ALLOCATION VERIFICATION REQUEST IS MISSING";
	private static final String IDENTITY_VERIFICATION_REQUEST = "IDENTITY VERIFICATION REQUEST IS MISSING";
	private static final String ERROR = "Error";
	
	
	public DepositAllocationRequestWrapperModel setDepositAllocationRequest(DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel, SdkCreateCustomerRequest sdkCreateCustomerRequest) {
		if(sdkCreateCustomerRequest != null) {
			
			if(sdkCreateCustomerRequest.getNumberOfInstallments() == null)
				depositAllocationRequestWrapperModel.setNumberOfInstallments(0);
	    	if(sdkCreateCustomerRequest.getInstallmentAmount() ==null)
	    		depositAllocationRequestWrapperModel.setInstallmentAmount(0);
	    	if(sdkCreateCustomerRequest.getLoanAmount() ==null)
	    		depositAllocationRequestWrapperModel.setLoanAmount(0);
			
	    	depositAllocationRequestWrapperModel.setEmployerId(sdkCreateCustomerRequest.getEmployerId());
	    	depositAllocationRequestWrapperModel.setEmailId(sdkCreateCustomerRequest.getEmailId());
	    	depositAllocationRequestWrapperModel.setCellPhone(sdkCreateCustomerRequest.getCellPhone());
	    	depositAllocationRequestWrapperModel.setLoanAmount(sdkCreateCustomerRequest.getLoanAmount());
//	    	depositAllocationRequestWrapperModel.setInstallmentAmount(sdkCreateCustomerRequest.getInstallmentAmount());
	    	depositAllocationRequestWrapperModel.setNumberOfInstallments(sdkCreateCustomerRequest.getNumberOfInstallments());
	    	depositAllocationRequestWrapperModel.setAchPullRequest(sdkCreateCustomerRequest.getAchPullRequest());
	    	depositAllocationRequestWrapperModel.setAccountVerificationOverride(sdkCreateCustomerRequest.getAccountVerificationOverride());
	    	depositAllocationRequestWrapperModel.setFirstDateOfPayment(sdkCreateCustomerRequest.getFirstDateOfPayment());
	    	depositAllocationRequestWrapperModel.setExternalVirtualAccount(sdkCreateCustomerRequest.getExternalVirtualAccount());
	    	depositAllocationRequestWrapperModel.setExternalVirtualAccountABANumber(sdkCreateCustomerRequest.getExternalVirtualAccountABANumber());
	    	depositAllocationRequestWrapperModel.setCallbackURLs(sdkCreateCustomerRequest.getCallbackURLs());
	    	depositAllocationRequestWrapperModel.setRepaymentFrequency(sdkCreateCustomerRequest.getRepaymentFrequency());
	    	depositAllocationRequestWrapperModel.setLender(sdkCreateCustomerRequest.getLender());
	    	depositAllocationRequestWrapperModel.setFirstName(sdkCreateCustomerRequest.getFirstName());
	    	depositAllocationRequestWrapperModel.setLastName(sdkCreateCustomerRequest.getLastName());
		}
		return depositAllocationRequestWrapperModel;
	}
	
	
	public EmploymentVerificationRequestWrapperModel setEmploymentVerification(EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel, SdkCreateCustomerRequest sdkCreateCustomerRequest) {
		if(sdkCreateCustomerRequest != null) {
			employmentVerificationRequestWrapperModel.setEmployerId(sdkCreateCustomerRequest.getEmployerId());
			employmentVerificationRequestWrapperModel.setFirstName(sdkCreateCustomerRequest.getFirstName());
			employmentVerificationRequestWrapperModel.setLastName(sdkCreateCustomerRequest.getLastName());
			employmentVerificationRequestWrapperModel.setLender(sdkCreateCustomerRequest.getLender());
			employmentVerificationRequestWrapperModel.setCallbackURLs(sdkCreateCustomerRequest.getCallbackURLs());
			employmentVerificationRequestWrapperModel.setCellPhone(sdkCreateCustomerRequest.getCellPhone());
			employmentVerificationRequestWrapperModel.setEmailId(sdkCreateCustomerRequest.getEmailId());
			
		}
		return employmentVerificationRequestWrapperModel;
	}
	
	
public IdentityVerificationRequestWrapperModel setCustomerRequestForIdentity(IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel, SdkCreateCustomerRequest sdkCreateCustomerRequest) {
		
		if(sdkCreateCustomerRequest != null) {
			identityVerificationRequestWrapperModel.setEmployerId(sdkCreateCustomerRequest.getEmployerId());
			identityVerificationRequestWrapperModel.setEmailId(sdkCreateCustomerRequest.getEmailId());
			identityVerificationRequestWrapperModel.setCellPhone(sdkCreateCustomerRequest.getCellPhone());			
			identityVerificationRequestWrapperModel.setCallbackURLs(sdkCreateCustomerRequest.getCallbackURLs());
	    	identityVerificationRequestWrapperModel.setFirstName(sdkCreateCustomerRequest.getFirstName());
	    	identityVerificationRequestWrapperModel.setLastName(sdkCreateCustomerRequest.getLastName());
	    	identityVerificationRequestWrapperModel.setAddressLine1(sdkCreateCustomerRequest.getAddressLine1());
	    	identityVerificationRequestWrapperModel.setAddressLine2(sdkCreateCustomerRequest.getAddressLine2());
	    	identityVerificationRequestWrapperModel.setDateOfBirth(sdkCreateCustomerRequest.getDateOfBirth());
	    	identityVerificationRequestWrapperModel.setLast4TIN(sdkCreateCustomerRequest.getLast4TIN());
	    	identityVerificationRequestWrapperModel.setCity(sdkCreateCustomerRequest.getCity());
	    	identityVerificationRequestWrapperModel.setState(sdkCreateCustomerRequest.getState());
	    	identityVerificationRequestWrapperModel.setZip(sdkCreateCustomerRequest.getZip());
	    	
			
		}
		return identityVerificationRequestWrapperModel;
	} 

public IncomeVerificationRequestWrapperModel setCustomerRequestForIncome(IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel, SdkCreateCustomerRequest sdkCreateCustomerRequest) {
	
	if(incomeVerificationRequestWrapperModel != null) {
		incomeVerificationRequestWrapperModel.setFirstName(sdkCreateCustomerRequest.getFirstName());
		incomeVerificationRequestWrapperModel.setLastName(sdkCreateCustomerRequest.getLastName());
		incomeVerificationRequestWrapperModel.setCellPhone(sdkCreateCustomerRequest.getCellPhone());
		incomeVerificationRequestWrapperModel.setEmailId(sdkCreateCustomerRequest.getEmailId());
		incomeVerificationRequestWrapperModel.setCallbackURLs(sdkCreateCustomerRequest.getCallbackURLs());
		incomeVerificationRequestWrapperModel.setEmployerId(sdkCreateCustomerRequest.getEmployerId());
		incomeVerificationRequestWrapperModel.setLender(sdkCreateCustomerRequest.getLender());
		incomeVerificationRequestWrapperModel.setNumberOfMonthsRequested(sdkCreateCustomerRequest.getNumberOfMonthsRequested());
		
	}
	return incomeVerificationRequestWrapperModel;
}

	public void validateCustomerRequest(SdkCreateCustomerRequest sdkCreateCustomerRequest, String requestId,
			LenderConfigInfo lenderConfigInfo, RequestIdDetails requestIdDetails) {
		DepositAllocationRequestWrapperModel depositAllocationRequestWrapperModel = null;
		EmploymentVerificationRequestWrapperModel employmentVerificationRequestWrapperModel = null;
		IdentityVerificationRequestWrapperModel identityVerificationRequestWrapperModel = null;
		IncomeVerificationRequestWrapperModel incomeVerificationRequestWrapperModel = null;

		List<ServicesSelectedEnum> servicesSelected = sdkCreateCustomerRequest.getServicesSelected();
		try {

			for (ServicesSelectedEnum flowTypeEnum : servicesSelected) {
				if (flowTypeEnum.equals(ServicesSelectedEnum.EMPLOYMENT_VERIFICATION)) {
					employmentVerificationRequestWrapperModel = new EmploymentVerificationRequestWrapperModel();
					employmentVerificationRequestWrapperModel = setEmploymentVerification(
							employmentVerificationRequestWrapperModel, sdkCreateCustomerRequest);
					if (employmentVerificationRequestWrapperModel != null) {
						customerWrapperAPIService.validateEmploymentVerificationRequest(
								employmentVerificationRequestWrapperModel, requestId, requestIdDetails,
								lenderConfigInfo);
					} else {
						throw new GeneralCustomException(ERROR, EMPLOYMENT_VERIFICATION_REQUEST);
					}
				} else if (flowTypeEnum.equals(ServicesSelectedEnum.INCOME_VERIFICATION)) {
					incomeVerificationRequestWrapperModel = new IncomeVerificationRequestWrapperModel();
					incomeVerificationRequestWrapperModel = setCustomerRequestForIncome(
							incomeVerificationRequestWrapperModel, sdkCreateCustomerRequest);
					if (incomeVerificationRequestWrapperModel != null) {
						customerWrapperAPIService.validateIncomeVerificationRequest(
								incomeVerificationRequestWrapperModel, requestId, requestIdDetails, lenderConfigInfo);
					} else {
						throw new GeneralCustomException(ERROR, INCOME_VERIFICATION_REQUEST);
					}
				} else if (flowTypeEnum.equals(ServicesSelectedEnum.DEPOSIT_ALLOCATION)) {
					depositAllocationRequestWrapperModel = new DepositAllocationRequestWrapperModel();
					depositAllocationRequestWrapperModel = setDepositAllocationRequest(
							depositAllocationRequestWrapperModel, sdkCreateCustomerRequest);
					if (depositAllocationRequestWrapperModel != null) {
						customerWrapperAPIService.validateDepositAllocationRequest(depositAllocationRequestWrapperModel,
								requestId, requestIdDetails, lenderConfigInfo);
					} else {

						throw new GeneralCustomException(ERROR, DEPOSIT_VERIFICATION_REQUEST);

					}
				} else if (flowTypeEnum.equals(ServicesSelectedEnum.IDENTITY_VERIFICATION)) {
					identityVerificationRequestWrapperModel = new IdentityVerificationRequestWrapperModel();
					identityVerificationRequestWrapperModel = setCustomerRequestForIdentity(
							identityVerificationRequestWrapperModel, sdkCreateCustomerRequest);
					if (identityVerificationRequestWrapperModel != null) {
						customerWrapperAPIService.validateIdentityVerificationRequest(
								identityVerificationRequestWrapperModel, requestId, requestIdDetails, lenderConfigInfo);
					}
				} else {
					throw new GeneralCustomException(ERROR, IDENTITY_VERIFICATION_REQUEST);
				}

			}
		} catch (Exception e) {
			Sentry.captureException(e);
			throw new GeneralCustomException(ERROR, e.getMessage());
		}

	}
}


