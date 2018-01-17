package com.example.data.exceptions;


/**
 * A supplier checked for by key is not present
 */
public class SupplierMissingViolation extends DataException {
    public SupplierMissingViolation(String s, Exception exception) {
        super(s, exception);
    }
}
