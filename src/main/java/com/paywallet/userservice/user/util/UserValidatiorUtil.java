package com.paywallet.userservice.user.util;

import org.apache.commons.lang.StringUtils;

import com.paywallet.userservice.user.entities.CreateUserRequest;

public class UserValidatiorUtil {

	public static boolean validateUserRequest(CreateUserRequest user) {
		if (StringUtils.isBlank(user.getFirstName()) || StringUtils.isBlank(user.getLastName()) || StringUtils.isBlank(user.getEmail())
				|| StringUtils.isEmpty(user.getPhoneNumber())) {
		return false;
		}
		return true;
	}
}
