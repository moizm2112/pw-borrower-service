package com.paywallet.userservice.user.model.wrapperAPI.income;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponseInfo {

    private String emailId;
    private String mobileNo;
    private String employer;

}
