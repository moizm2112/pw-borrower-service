package com.paywallet.userservice.user.model;

import java.util.Date;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerAccountResponseDTO {
    private AccountDetails data;
    private String message;
    private int status;
    private Date timeStamp;
    private String path;
}
