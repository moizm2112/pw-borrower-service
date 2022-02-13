package com.paywallet.userservice.user.model;

import lombok.Data;


@Data
public class FineractUpdateLenderResponseDTO {

    private Long officeId;
    private Long clientId;
    private Long resourceId;
    private FineractUpdateLenderAccountDTO changes;
}
