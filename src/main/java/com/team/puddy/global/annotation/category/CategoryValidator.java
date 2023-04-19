package com.team.puddy.global.annotation.category;


import javax.validation.ConstraintValidator;

public class CategoryValidator implements ConstraintValidator<Category, String> {
    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            com.team.puddy.domain.type.Category.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return true;
    }
}
