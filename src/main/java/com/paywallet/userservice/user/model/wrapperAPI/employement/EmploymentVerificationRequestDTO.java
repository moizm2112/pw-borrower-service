package com.paywallet.userservice.user.model.wrapperAPI.employement;

import com.paywallet.userservice.user.model.CallbackURL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmploymentVerificationRequestDTO {

    private String employerId;
    private String emailId;
    private String mobileNo;
    private String firstName;
    private String lastName;
    private String middleName;
    private CallbackURL callbackUrls;

}
