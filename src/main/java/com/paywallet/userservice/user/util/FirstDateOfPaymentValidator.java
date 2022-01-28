package com.paywallet.userservice.user.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.paywallet.userservice.user.exception.GeneralCustomException;

public class FirstDateOfPaymentValidator implements ConstraintValidator<FirstDateOfPaymentCheck, String>{

	@Override
	public boolean isValid(String date, ConstraintValidatorContext context) {
		boolean valid = false;
		
        try {
        	if(date != null) {
        	    Date currentDate = new Date();  
        	    // First Date Of Payment
        		Date firstDateOfPayment = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        		// Current Date
    			Date currentDateFormated = new SimpleDateFormat("yyyy-MM-dd").parse(currentDate.toString());
    			
    			if(firstDateOfPayment.after(currentDateFormated))
    				valid = true;
    			else
    				valid = false;
        	}else {
        		valid = true;
        	}
        } catch (DateTimeParseException e) {
        	valid = false;
        	throw new GeneralCustomException("ERROR", e.getMessage());
        } catch (ParseException e) {
        	valid = false;
        	throw new GeneralCustomException("ERROR", e.getMessage());
		}
        return valid;	
	}

}
