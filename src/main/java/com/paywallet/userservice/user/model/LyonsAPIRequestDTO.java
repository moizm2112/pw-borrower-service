package com.paywallet.userservice.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class LyonsAPIRequestDTO {

	private String token;

	private int returnDetails;

	private AccountStatusRequest accountStatusRequest;

	@JsonIgnore
	private String abaNumber;

	@JsonIgnore
	private String accountNumber;

	@JsonIgnore
	private String firstName;

	@JsonIgnore
	private String lastName;

	@JsonIgnore
	private String businessName;

	@JsonIgnore
	private String postalCode;

	@JsonIgnore
	private String middleName;

	@JsonIgnore
	private Date dateOfBirth;

	public void initRequest() {
		accountStatusRequest = new AccountStatusRequest(this);
	}

	@Data
	private final class AccountStatusRequest {

		String rtn;
		String accountNo;
		AccountOwner accountOwner;

		AccountStatusRequest(LyonsAPIRequestDTO dto) {
			this.rtn = dto.abaNumber;
			this.accountNo = dto.accountNumber;
			accountOwner = new AccountOwner(dto);
		}

		@Data
		private final class AccountOwner {

			String firstName;
			String middleName;
			String lastName;
			String businessName;
			String zip;

			AccountOwner(LyonsAPIRequestDTO dto) {
				this.firstName = dto.firstName;
				this.lastName = dto.lastName;
				this.middleName = dto.middleName;
				this.businessName = dto.businessName;
				this.zip = dto.postalCode;
			}
		}
	}
}
