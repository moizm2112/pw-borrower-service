package com.paywallet.userservice.user.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class FineractLenderCreationResponseDTO {
    private Long resourceId;
    private Long clientId;
    private Long officeId;
    private Long savingsId;
}
