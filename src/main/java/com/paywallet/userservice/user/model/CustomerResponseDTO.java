package com.paywallet.userservice.user.model;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.paywallet.userservice.user.entities.CustomerDetails;

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
public class CustomerResponseDTO {
    private CustomerDetails data;
    private String message;
    private int status;
    private Date timeStamp;
    private String path;
    private String requestId;
}
