package com.paywallet.userservice.user.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.paywallet.userservice.user.entities.CustomerDetails;
import com.paywallet.userservice.user.entities.PersonalProfile;
import com.paywallet.userservice.user.entities.SalaryProfile;
import com.paywallet.userservice.user.model.CreateCustomerRequest;
import com.paywallet.userservice.user.model.FineractCreateLenderDTO;
import com.paywallet.userservice.user.model.FineractLenderAddressDTO;

@Component
public class CustomerServiceHelper {
	
	public void setCustomerDetails(CreateCustomerRequest customer, CustomerDetails custDtls) {
        PersonalProfile personalProfileToUpdate = custDtls.getPersonalProfile();
        personalProfileToUpdate.setFirstName(customer.getFirstName());
        personalProfileToUpdate.setLastName(customer.getLastName());
        personalProfileToUpdate.setEmailId(customer.getEmailId());
        personalProfileToUpdate.setZip(customer.getZip());
        personalProfileToUpdate.setState(customer.getState());
        personalProfileToUpdate.setDateOfBirth(customer.getDateOfBirth());
        personalProfileToUpdate.setLast4TIN(customer.getLast4TIN());
        personalProfileToUpdate.setAddressLine1(customer.getAddressLine1());
        personalProfileToUpdate.setAddressLine2(customer.getAddressLine2());
        personalProfileToUpdate.setMiddleName(customer.getMiddleName());
//        custDtls.setFinancedAmount(customer.getFinancedAmount());
//        custDtls.setAbaOfSalaryAccount(customer.getBankABA());
//        custDtls.setSalaryAccountNumber(customer.getBankAccountNumber());
    }

	public CustomerDetails buildCustomerDetails(CreateCustomerRequest customer) {
		PersonalProfile personalProfile = PersonalProfile.builder().firstName(customer.getFirstName())
				.lastName(customer.getLastName()).emailId(customer.getEmailId()).mobileNo(customer.getMobileNo())
				.middleName(customer.getMiddleName()).addressLine1(customer.getAddressLine1())
				.addressLine2(customer.getAddressLine2()).zip(customer.getZip()).city(customer.getCity()).state(customer.getState())
				.last4TIN(customer.getLast4TIN()).dateOfBirth(customer.getDateOfBirth()).build();

		CustomerDetails customerEntity = CustomerDetails.builder().personalProfile(personalProfile).build();
//         		.financedAmount(customer.getFinancedAmount()).financedAmount(customer.getFinancedAmount())
//         		.abaOfSalaryAccount(customer.getBankABA()).salaryAccountNumber(customer.getBankAccountNumber()).build();

		return customerEntity;
	}

	public FineractCreateLenderDTO setFineractDataToCreateAccount(CustomerDetails customerEntity) throws ParseException {
		FineractCreateLenderDTO fineractCreateAccountDTO = new FineractCreateLenderDTO();
		FineractLenderAddressDTO fineractLenderAddressDTO = new FineractLenderAddressDTO();
		Set<FineractLenderAddressDTO> sFineractLenderAddress = new HashSet<FineractLenderAddressDTO>();
		fineractLenderAddressDTO.setAddressTypeId("1");
		fineractLenderAddressDTO.setAddressLine1(customerEntity.getPersonalProfile().getAddressLine1());
		fineractLenderAddressDTO.setIsActive(true);
		fineractLenderAddressDTO.setStateProvinceId(Long.valueOf("1"));
		fineractLenderAddressDTO.setCountryId(Long.valueOf("1"));
		sFineractLenderAddress.add(fineractLenderAddressDTO);
		fineractCreateAccountDTO.setFirstname(customerEntity.getPersonalProfile().getFirstName());
		fineractCreateAccountDTO.setLastname(customerEntity.getPersonalProfile().getLastName());
//    	fineractCreateAccountDTO.setFullname(customerEntity.getPersonalProfile().getFirstName()+" "+ customerEntity.getPersonalProfile().getLastName());
		fineractCreateAccountDTO.setExternalId(customerEntity.getPersonalProfile().getMobileNo());
		fineractCreateAccountDTO.setMobileNo(customerEntity.getPersonalProfile().getMobileNo());

		fineractCreateAccountDTO.setDateFormat("dd MMMM yyyy");
		fineractCreateAccountDTO.setLocale("en");
		fineractCreateAccountDTO.setActive(true);
		DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = df1.parse(customerEntity.getPersonalProfile().getDateOfBirth());
		String requiredDate = df.format(new Date()).toString();

		String dateOfBirth = df.format(d1);
		fineractCreateAccountDTO.setDateOfBirth(dateOfBirth);
		fineractCreateAccountDTO.setActivationDate(requiredDate);
		fineractCreateAccountDTO.setSubmittedOnDate(requiredDate);
		fineractCreateAccountDTO.setOfficeId(Long.valueOf("1"));
		fineractCreateAccountDTO.setClientTypeId(Long.valueOf("18"));
		fineractCreateAccountDTO.setLegalFormId(Long.valueOf("2"));
		fineractCreateAccountDTO.setSavingsProductId(Long.valueOf("2"));
		fineractCreateAccountDTO.setAddress(sFineractLenderAddress);
		return fineractCreateAccountDTO;

	}

}
