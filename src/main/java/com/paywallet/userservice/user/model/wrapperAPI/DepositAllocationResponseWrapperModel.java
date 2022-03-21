package com.paywallet.userservice.user.model.wrapperAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class DepositAllocationResponseWrapperModel {

	private String emailId;
	private String cellPhone;
	private double installmentAmount;
	private Integer numberOfInstallments;
	private String virtualAccountNumber;
	@JsonInclude(value = Include.NON_NULL)
	private String virtualAccountABANumber;
	@JsonInclude(value = Include.NON_NULL)
	private String virtualAccountId;
}

