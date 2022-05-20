package com.paywallet.userservice.user.util;

import com.paywallet.userservice.user.dto.PayrollProviderDetailsDTO;
import com.paywallet.userservice.user.entities.PayrollProfile;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface PayrollProfileObjectMapper {
    PayrollProfile convertToProfile(PayrollProviderDetailsDTO payrollProviderDetailsDTO);
    PayrollProviderDetailsDTO convertToDTO(PayrollProfile payrollProfile);
}
