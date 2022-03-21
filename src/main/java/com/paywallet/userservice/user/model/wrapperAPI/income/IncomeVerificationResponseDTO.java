package com.paywallet.userservice.user.model.wrapperAPI.income;

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
public class IncomeVerificationResponseDTO {

    private IncomeResponseInfo data;
    private String message;
    private String status;
    private Date timeStamp;
    private String path;

}
