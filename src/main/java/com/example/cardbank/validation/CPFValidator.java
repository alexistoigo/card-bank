package com.example.cardbank.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {

    @Override
    public void initialize(CPF constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return true;
        }

        if (!cpf.matches("[\\d.-]+")) {
            return false;
        }

        String apenasDigitos = cpf.replaceAll("\\D", "");
        return apenasDigitos.length() == 11;
    }
}
