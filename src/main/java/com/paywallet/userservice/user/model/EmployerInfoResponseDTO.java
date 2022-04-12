package com.paywallet.userservice.user.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployerInfoResponseDTO {

    private EmployerInfo data;
    private String message;
    private int status;
    private Date timeStamp;
    private String path;

}