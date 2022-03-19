package com.paywallet.userservice.user.model.wrapperAPI.employement;

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
public class EmploymentResponseInfo {

    private String emailId;
    private String mobileNo;
    private String employer;

}
