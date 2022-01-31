package com.paywallet.userservice.user.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RepaymentFrequencyModeValidator.class)
@Documented
public @interface RepaymentFrequencyMode {
	
	String message() default
    "Enter valid Repayment frequency mode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
