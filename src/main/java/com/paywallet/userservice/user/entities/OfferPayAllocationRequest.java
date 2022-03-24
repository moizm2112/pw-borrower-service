package com.paywallet.userservice.user.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferPayAllocationRequest {
	private String loanAmount;
    private String installmentAmount;
    private int numberOfInstallment;
    private String firstRepaymentDate;

}
