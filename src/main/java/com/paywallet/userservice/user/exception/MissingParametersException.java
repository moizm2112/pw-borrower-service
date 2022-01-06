package com.paywallet.userservice.user.exception;

public class MissingParametersException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MissingParametersException(String message) {
		super(message);
	}

}
