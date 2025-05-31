package com.worklyze.worklyze.application.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotZeroAndNotNullValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotZeroAndNotNullConstraint {
    String message() default "O valor passado não pode ser 0 ou null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
