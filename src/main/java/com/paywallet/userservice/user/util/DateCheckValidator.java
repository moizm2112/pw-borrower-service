package com.paywallet.userservice.user.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.paywallet.userservice.user.exception.GeneralCustomException;

public class DateCheckValidator implements ConstraintValidator<DateCheck, String>{

	@Override
	public boolean isValid(String date, ConstraintValidatorContext context) {
		boolean valid = false;
		
        try {
        	if(date != null) {
        		 LocalDate.parse(date,
                         DateTimeFormatter.ofPattern("uuuu-M-d")
                                 .withResolverStyle(ResolverStyle.STRICT)
                 );
                 valid = true;
        	}else {
        		valid = true;
        	}
            // ResolverStyle.STRICT for 30, 31 days checking, and also leap year.
           
        } catch (DateTimeParseException e) {
        	valid = false;
        	throw new GeneralCustomException("ERROR", e.getMessage());
            
        }
        return valid;	
	}

}
