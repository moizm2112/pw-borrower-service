package com.paywallet.userservice.user.entities;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.mongodb.lang.NonNull;

import lombok.Data;

@Data
public class FindUserRequest {

	@Id
	private String id;

	@NonNull
	private String firstName;

	@NonNull
	private String lastName;

	private String middleName;

	private String createdAt;

	private String updateAt;

	@NonNull
	private String email;

	@NonNull
	private String phoneNumber;

	private String ssn;

	private List<String> employers;

	private String role;

	private String address;

	private String communicationMode;

	private String paswword;
}
