package com.paywallet.userservice.user.services;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.model.UpdateCustomerCredentialStatus;
import com.paywallet.userservice.user.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerCredentialVerificationService {

	@Autowired
	CustomerRepository customerRepo;

	public CustomerDetails updateCustCredVerificationStatus(String requestId,
			UpdateCustomerCredentialStatus customerCredentialStatus) {
		CustomerDetails custdetails = null;

		CustomerDetails customerDetailFromDb = customerRepo.findByCustomerId(customerCredentialStatus.getCustomerId())
				.orElseThrow(() -> new CustomerNotFoundException(
						"Customer not present with the customerId : " + customerCredentialStatus.getCustomerId()
								+ " to fetch customer details for request id : " + requestId));
		custdetails = customerRepo.save(prepareUpdatedCustomerObj(customerDetailFromDb, customerCredentialStatus));
		log.info("updated customer details :: "+custdetails);
		return custdetails;
	}

	private CustomerDetails prepareUpdatedCustomerObj(CustomerDetails customerDetailFromDb,
			UpdateCustomerCredentialStatus customerCredentialStatus) {

		if (Objects.nonNull(customerCredentialStatus.getCellPhoneVerificationStatus())) {
			customerDetailFromDb.setCellPhoneVerificationStatus(customerCredentialStatus.getCellPhoneVerificationStatus());
        }
        if (Objects.nonNull(customerCredentialStatus.getEmailIdVerificationStatus())) {
			customerDetailFromDb.setEmailIdVerificationStatus(customerCredentialStatus.getEmailIdVerificationStatus());
        }
		return customerDetailFromDb;
	}

	public ResponseEntity<Object> prepareUpdateResponse(Object CustomerCredentialsStatus, String message,
			int status, String path) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("data", CustomerCredentialsStatus);
		body.put("message", message);
		body.put("status", status);
		body.put("timestamp", new Date());
		body.put("path", path);
		return new ResponseEntity<>(body, HttpStatus.OK);
	}

}

