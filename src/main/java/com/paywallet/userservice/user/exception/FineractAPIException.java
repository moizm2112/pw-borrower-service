package com.paywallet.userservice.user.exception;

public class FineractAPIException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FineractAPIException(String message) {
		super(message);
	}
}
