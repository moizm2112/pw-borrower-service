package com.paywallet.userservice.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class GeneralCustomException extends RuntimeException {

    private static final long serialVersionUID = -7201163830646384086L;
    private final HashMap<String,String> errors = new HashMap<>();
    public GeneralCustomException(String parameter, String message) {
        super(message);
        this.errors.put(parameter, message);
    }

    public GeneralCustomException() {
        super();
    }

    public GeneralCustomException append(String parameter, String message){
        this.errors.put(parameter, message);
        return this;
    }

    public GeneralCustomException clear(){
        this.errors.clear();
        return this;
    }

    public Map<String,String> getErrors(){
        return this.errors;
    }
}

