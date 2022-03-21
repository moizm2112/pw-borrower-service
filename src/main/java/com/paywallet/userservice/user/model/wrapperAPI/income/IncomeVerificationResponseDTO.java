package com.paywallet.userservice.user.model.wrapperAPI.income;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;

@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class IncomeVerificationResponseDTO {
	@JsonInclude(Include.NON_NULL)
    private IncomeResponseInfo data;
    private String message;
    private String status;
    private Date timeStamp;
    private String path;

}
