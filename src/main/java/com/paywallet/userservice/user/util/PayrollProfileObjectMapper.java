package com.paywallet.userservice.user.util;

import com.paywallet.userservice.user.dto.PayrollProviderDetailsDTO;
import com.paywallet.userservice.user.entities.PayrollProfile;
import org.springframework.stereotype.Component;

@Component
public class PayrollProfileObjectMapper {

    public PayrollProfile convertToProfile(PayrollProviderDetailsDTO payrollProviderDetailsDTO) {
        PayrollProfile payrollProfile = new PayrollProfile();
        payrollProfile.setFirstName(payrollProviderDetailsDTO.getFirstName());
        payrollProfile.setLastName(payrollProviderDetailsDTO.getLastName());
        payrollProfile.setMiddleName(payrollProviderDetailsDTO.getMiddleName());
        payrollProfile.setAddressLine1(payrollProviderDetailsDTO.getAddressLine1());
        payrollProfile.setAddressLine2(payrollProviderDetailsDTO.getAddressLine2());
        payrollProfile.setCity(payrollProviderDetailsDTO.getCity());
        payrollProfile.setCellPhone(payrollProviderDetailsDTO.getCellPhone());
        payrollProfile.setEmailId(payrollProviderDetailsDTO.getEmailId());
        payrollProfile.setDateOfBirth(payrollProviderDetailsDTO.getDateOfBirth());
        payrollProfile.setLast4TIN(payrollProviderDetailsDTO.getLast4TIN());
        payrollProfile.setState(payrollProviderDetailsDTO.getState());
        payrollProfile.setZip(payrollProviderDetailsDTO.getZip());
        return payrollProfile;
    }

    public PayrollProviderDetailsDTO convertToDTO(PayrollProfile payrollProfile) {
        PayrollProviderDetailsDTO payrollProviderDetailsDTO = new PayrollProviderDetailsDTO();
        payrollProviderDetailsDTO.setFirstName(payrollProfile.getFirstName());
        payrollProviderDetailsDTO.setLastName(payrollProfile.getLastName());
        payrollProviderDetailsDTO.setMiddleName(payrollProfile.getMiddleName());
        payrollProviderDetailsDTO.setAddressLine1(payrollProfile.getAddressLine1());
        payrollProviderDetailsDTO.setAddressLine2(payrollProfile.getAddressLine2());
        payrollProviderDetailsDTO.setCity(payrollProfile.getCity());
        payrollProviderDetailsDTO.setCellPhone(payrollProfile.getCellPhone());
        payrollProviderDetailsDTO.setEmailId(payrollProfile.getEmailId());
        payrollProviderDetailsDTO.setDateOfBirth(payrollProfile.getDateOfBirth());
        payrollProviderDetailsDTO.setLast4TIN(payrollProfile.getLast4TIN());
        payrollProviderDetailsDTO.setState(payrollProfile.getState());
        payrollProviderDetailsDTO.setZip(payrollProfile.getZip());
        return payrollProviderDetailsDTO;
    }
}
