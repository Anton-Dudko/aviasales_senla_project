package com.aviasales.finance.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CardDateValidator.class})
public @interface ValidCardDate {
    String message() default "Invalid card date, card date should not be empty and month current or in future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
