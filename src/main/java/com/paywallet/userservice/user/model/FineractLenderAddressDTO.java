package com.paywallet.userservice.user.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class FineractLenderAddressDTO {

    private String addressTypeId;
    private String addressLine1;
    private Boolean isActive;
//    private String street;
    private Long stateProvinceId;
    private Long countryId;
}
