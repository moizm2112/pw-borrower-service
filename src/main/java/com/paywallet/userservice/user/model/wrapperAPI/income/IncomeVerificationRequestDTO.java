package com.paywallet.userservice.user.model.wrapperAPI.income;

import com.paywallet.userservice.user.model.CallbackURL;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class IncomeVerificationRequestDTO {

    private String employerId;
    private String emailId;
    private String cellPhone;
    private String firstName;
    private String lastName;
    private String middleName;
    private CallbackURL callbackUrls;

}
