package com.paywallet.userservice.user.services;

import org.springframework.stereotype.Service;

import com.paywallet.userservice.user.model.SdkCreateCustomerRequest;
import com.paywallet.userservice.user.model.wrapperAPI.DepositAllocationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.EmploymentVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IdentityVerificationRequestWrapperModel;
import com.paywallet.userservice.user.model.wrapperAPI.IncomeVerificationRequestWrapperModel;

@Service
public class SdkCustomerServiceHelper {

	
	
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

}
