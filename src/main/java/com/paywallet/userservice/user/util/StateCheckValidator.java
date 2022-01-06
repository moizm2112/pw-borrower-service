package com.paywallet.userservice.user.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.paywallet.userservice.user.enums.StateEnum;

public class StateCheckValidator implements ConstraintValidator<StateCheck, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		List<StateEnum> l = Arrays.asList(StateEnum.values());
		Iterator<StateEnum> i = l.iterator();
		while(i.hasNext())
		{
			if(i.next().toString().equalsIgnoreCase(value))
				return true;
		}
		return false;	
	}
}
