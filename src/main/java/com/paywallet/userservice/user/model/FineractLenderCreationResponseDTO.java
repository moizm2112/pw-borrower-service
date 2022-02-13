package com.paywallet.userservice.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class FineractLenderCreationResponseDTO {
	@JsonInclude(JsonInclude.Include. NON_NULL)
    private Long resourceId;
	@JsonInclude(JsonInclude.Include. NON_NULL)
    private Long clientId;
	@JsonInclude(JsonInclude.Include. NON_NULL)
    private Long officeId;
	@JsonInclude(JsonInclude.Include. NON_NULL)
    private Long savingsId;
    @JsonInclude(JsonInclude.Include. NON_NULL)
    private String savingsAccountNumber;
    @JsonInclude(JsonInclude.Include. NON_NULL)
    private String gsimId;
}
