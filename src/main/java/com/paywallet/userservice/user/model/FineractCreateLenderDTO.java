package com.paywallet.userservice.user.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Getter
@Setter
public class FineractCreateLenderDTO {
    private String firstname;
    private String lastname;
    private String fullname;
//    private String displayName;
    private String externalId;
    private String mobileNo;
    private String dateOfBirth;
    private String dateFormat; //"dd MMMM yyyy"
    private String locale; //en
    private Boolean active;
    private String activationDate;
    private String submittedOnDate;
    private Long officeId;
    private Long clientTypeId;
    private Long legalFormId;
    private Long savingsProductId;
    private Set<FineractLenderAddressDTO> address;
}
