package com.example.cardbank.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CPFValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CPF {
    String message() default "CPF inválido. Deve conter 11 dígitos e apenas os caracteres permitidos.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}