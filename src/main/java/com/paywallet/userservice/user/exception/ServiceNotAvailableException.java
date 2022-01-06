package com.paywallet.userservice.user.exception;

import java.util.HashMap;
import java.util.Map;

public class ServiceNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    private final HashMap<String,String> errors = new HashMap<>();
    public ServiceNotAvailableException(String parameter, String message) {
        super(message);
        this.errors.put(parameter, message);
    }

    public ServiceNotAvailableException() {
        super();
    }

    public ServiceNotAvailableException append(String parameter, String message){
        this.errors.put(parameter, message);
        return this;
    }

    public ServiceNotAvailableException clear(){
        this.errors.clear();
        return this;	
    }

    public Map<String,String> getErrors(){
        return this.errors;
    }
}
