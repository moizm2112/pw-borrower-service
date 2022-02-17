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
@Constraint(validatedBy = AddRequiredFieldsValidator.class)
@Documented
public @interface AddRequiredFieldsCheck {

	String message() default
    "Only YES (Mandatory fields) and NO (Non-Mandatory fields) Strings are accepted";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
}
