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
import io.sentry.Sentry;

/**
 * This class validates first data of payment field with date validation and field value should greater than current date.
 * @author paywallet
 *
 */
public class FirstDateOfPaymentValidator implements ConstraintValidator<FirstDateOfPaymentCheck, String>{

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
        		Date firstDateOfPayment = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        		// Current Date
    			Date currentDateFormated = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(currentDate));
    			
    			if(firstDateOfPayment.after(currentDateFormated))
    				valid = true;
    			else
    				valid = false;
        	}else {
        		// Setting to True as this field is an optional field. if its null, assuming that its not available in the request.
        		valid = true;
        	}
        } catch (DateTimeParseException e) {
			Sentry.captureException(e);
        	valid = false;
        	throw new GeneralCustomException("ERROR", e.getMessage());
        } catch (ParseException e) {
			Sentry.captureException(e);
        	valid = false;
        	throw new GeneralCustomException("ERROR", e.getMessage());
		}
        return valid;	
	}

}
