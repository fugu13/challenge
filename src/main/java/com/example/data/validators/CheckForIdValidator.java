package com.example.data.validators;

import com.example.domain.Supplier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validate that a Supplier has an ID
 * <p>
 * Used inside Transaction, as the other Supplier requirements do not apply.
 */
public class CheckForIdValidator implements ConstraintValidator<CheckForId, Supplier> {

    @Override
    public void initialize(CheckForId checkForId) {

    }

    @Override
    public boolean isValid(Supplier supplier, ConstraintValidatorContext constraintValidatorContext) {
        if (supplier == null) {
            return true;
        } else {
            if (supplier.getId() == null) {
                return false;
            } else {
                return true;
            }
        }
    }
}
