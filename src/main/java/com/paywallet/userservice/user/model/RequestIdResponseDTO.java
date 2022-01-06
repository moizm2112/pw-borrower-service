package com.paywallet.userservice.user.model;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestIdResponseDTO {
    private RequestIdDetails data;
    private String message;
    private int status;
    private Date timeStamp;
    private String path;
}
