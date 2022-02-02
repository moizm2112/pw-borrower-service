package com.paywallet.userservice.user.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.paywallet.userservice.user.exception.GeneralCustomException;

/**
 * This class validates Date of birth field with date validation and field value should be lesser than current date.
 * @author paywallet
 *
 */
public class DateCheckValidator implements ConstraintValidator<DateCheck, String>{

	@Override
	public boolean isValid(String date, ConstraintValidatorContext context) {
		boolean valid = false;
		
        try {
        	if(date != null) {
        		// ResolverStyle.STRICT for 30, 31 days checking, and also leap year.
        		LocalDate.parse(date,
                         DateTimeFormatter.ofPattern("uuuu-M-d")
                                 .withResolverStyle(ResolverStyle.STRICT));
        		 
        		Date currentDate = new Date();  
         	    // First Date Of Payment
         		Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(date);
         		// Current Date
     			Date currentDateFormated = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(currentDate));
     			
     			if(dateOfBirth.before(currentDateFormated))
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