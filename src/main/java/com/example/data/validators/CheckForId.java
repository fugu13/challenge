package com.example.data.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckForIdValidator.class)
public @interface CheckForId {
    String message() default "{com.example.data.validators.CheckForId.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
