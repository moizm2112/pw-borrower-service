package com.paywallet.userservice.user.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.paywallet.userservice.user.enums.AddRequiredFieldsEnum;

public class AddRequiredFieldsValidator implements ConstraintValidator<AddRequiredFieldsCheck, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		List<AddRequiredFieldsEnum> l = Arrays.asList(AddRequiredFieldsEnum.values());
		Iterator<AddRequiredFieldsEnum> i = l.iterator();
		while(i.hasNext())
		{
			if(i.next().toString().equalsIgnoreCase(value))
				return true;
		}
		return false;	
	}
}
