package com.paywallet.userservice.user.entities;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class OfferPayAllocationResponse {

    private String message;
    private String status;

}
