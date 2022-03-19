package com.paywallet.userservice.user.model.wrapperAPI.identity;

import com.paywallet.userservice.user.model.CallbackURL;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class IdentityVerificationRequestDTO {

    private String employerId;
    private String emailId;
    private String mobileNo;
    private String firstName;
    private String lastName;
    private String middleName;
    private CallbackURL callbackUrls;

}
