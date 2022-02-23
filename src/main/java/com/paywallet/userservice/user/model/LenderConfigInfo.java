package com.paywallet.userservice.user.model;

import org.springframework.stereotype.Component;

import com.paywallet.userservice.user.enums.StateStatus;

import lombok.Data;

@Component
@Data
public class LenderConfigInfo {

    private String lenderId;
    private String lenderName;
    private StateStatus publishIdentityInfo;
    private StateStatus publishEmploymentInfo;
    private StateStatus publishIncomeInfo;
    private StateStatus validateSalaryAccountOwnership;
    private StateStatus validateAffordabilityCheck;
    private StateStatus invokeAndPublishDepositAllocation;
	public String getLenderId() {
		return lenderId;
	}
	public void setLenderId(String lenderId) {
		this.lenderId = lenderId;
	}
	public String getLenderName() {
		return lenderName;
	}
	public void setLenderName(String lenderName) {
		this.lenderName = lenderName;
	}
	public StateStatus getPublishIdentityInfo() {
		return publishIdentityInfo;
	}
	public void setPublishIdentityInfo(StateStatus publishIdentityInfo) {
		this.publishIdentityInfo = publishIdentityInfo;
	}
	public StateStatus getPublishEmploymentInfo() {
		return publishEmploymentInfo;
	}
	public void setPublishEmploymentInfo(StateStatus publishEmploymentInfo) {
		this.publishEmploymentInfo = publishEmploymentInfo;
	}
	public StateStatus getPublishIncomeInfo() {
		return publishIncomeInfo;
	}
	public void setPublishIncomeInfo(StateStatus publishIncomeInfo) {
		this.publishIncomeInfo = publishIncomeInfo;
	}
	public StateStatus getValidateSalaryAccountOwnership() {
		return validateSalaryAccountOwnership;
	}
	public void setValidateSalaryAccountOwnership(StateStatus validateSalaryAccountOwnership) {
		this.validateSalaryAccountOwnership = validateSalaryAccountOwnership;
	}
	public StateStatus getValidateAffordabilityCheck() {
		return validateAffordabilityCheck;
	}
	public void setValidateAffordabilityCheck(StateStatus validateAffordabilityCheck) {
		this.validateAffordabilityCheck = validateAffordabilityCheck;
	}
	public StateStatus getInvokeAndPublishDepositAllocation() {
		return invokeAndPublishDepositAllocation;
	}
	public void setInvokeAndPublishDepositAllocation(StateStatus invokeAndPublishDepositAllocation) {
		this.invokeAndPublishDepositAllocation = invokeAndPublishDepositAllocation;
	}

}
