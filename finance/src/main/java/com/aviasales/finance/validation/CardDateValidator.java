package com.aviasales.finance.validation;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.YearMonth;

@Component
public class CardDateValidator implements ConstraintValidator<ValidCardDate, YearMonth> {

    @Override
    public boolean isValid(YearMonth date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            //field is mandatory for now
            return false;
        }
        return !YearMonth.now().isAfter(date);
    }
}
