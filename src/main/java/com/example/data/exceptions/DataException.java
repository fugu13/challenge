package com.example.data.exceptions;

/**
 * Exception superclass for DAO exceptions
 */
public class DataException extends Exception {
    public DataException(String s, Exception exception) {
        super(s, exception);
    }
}
