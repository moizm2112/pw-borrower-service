package com.paywallet.userservice.user.model.wrapperAPI.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerificationResponseDTO {

    private IdentityResponseInfo data;
    private String message;
    private int status;
    private Date timeStamp;
    private String path;

}
