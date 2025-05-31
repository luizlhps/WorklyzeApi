package com.worklyze.worklyze.application.validations;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class NotZeroAndNotNullValidator implements ConstraintValidator<NotZeroAndNotNullConstraint, BigDecimal> {

    @Override
    public void initialize(NotZeroAndNotNullConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BigDecimal bigDecimal, ConstraintValidatorContext constraintValidatorContext) {
        return bigDecimal != null && bigDecimal.compareTo(BigDecimal.ZERO) != 0;
    }
}

