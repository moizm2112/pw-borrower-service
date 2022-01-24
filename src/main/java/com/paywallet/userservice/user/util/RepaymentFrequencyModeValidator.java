package com.paywallet.userservice.user.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.paywallet.userservice.user.enums.RepaymentFrequencyModeEnum;

public class RepaymentFrequencyModeValidator implements ConstraintValidator<RepaymentFrequencyMode, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		List<RepaymentFrequencyModeEnum> l = Arrays.asList(RepaymentFrequencyModeEnum.values());
		Iterator<RepaymentFrequencyModeEnum> i = l.iterator();
		while(i.hasNext())
		{
			if(i.next().toString().equalsIgnoreCase(value) || value == null)
				return true;
		}
		return false;	
	}

}
