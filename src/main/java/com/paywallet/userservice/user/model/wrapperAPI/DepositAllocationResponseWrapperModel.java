package com.paywallet.userservice.user.model.wrapperAPI;

import lombok.Data;

@Data
public class DepositAllocationResponseWrapperModel {

	private String emailId;
	private String mobileNo;
	private Integer installmentAmount;
	private Integer totalNoOfRepayment;
	private String externalVirtualAccount;
	private String externalVirtualAccountABANumber;
}

