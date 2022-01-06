package com.paywallet.userservice.user.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountProfile {
    private String fineractAccount;
    private String abaOfSalaryAcct;
}
