package com.paywallet.userservice.user.model.wrapperAPI.employement;

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
public class EmploymentVerificationResponseDTO {

    private EmploymentResponseInfo data;
    private String message;
    private int status;
    private Date timeStamp;
    private String path;

}
