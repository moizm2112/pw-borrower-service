package com.paywallet.userservice.user.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
@Getter
@Setter
public class User {
	@Id
	private String id;
	private String firstName;
	private String lastName;
	private String middleName;
	private String createdAt;
	private String updateAt;
	private String email;
	private String phoneNumber;
	private String ssn;
	private List<String> employers;
	private String role;
	private String address;
	private String communicationMode;
	private String paswword;
}
