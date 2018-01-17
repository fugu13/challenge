package com.example.controller;

import com.example.data.exceptions.SupplierMissingViolation;
import com.example.data.exceptions.UniqueViolation;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Translate DAO exceptions to HTTP responses for the SupplierController
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(assignableTypes = SupplierController.class)
@RequestMapping(produces = "application/vnd.error+json")
public class SupplierExceptionAdvice {

    /**
     * Attempting to create a supplier with a duplicate name is a bad request
     *
     * @param e
     * @return bad request
     */
    @ExceptionHandler(UniqueViolation.class)
    public ResponseEntity<VndErrors> noDuplicateNamesException(final UniqueViolation e) {
        return error(e.getMessage(), HttpStatus.BAD_REQUEST, "duplicate");
    }

    /**
     * Attempting to retrieve a nonexistent supplier means it is not found
     *
     * @param e
     * @return not found
     */
    @ExceptionHandler(SupplierMissingViolation.class)
    public ResponseEntity<VndErrors> missingSupplierException(final SupplierMissingViolation e) {
        return error(e.getMessage(), HttpStatus.NOT_FOUND, "missing");
    }


    /**
     * Sending a supplier with validation errors is a bad request
     *
     * @param e
     * @return bad request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<VndErrors> validationException(final MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        return error("Problem with " + error.getField() + ", " + error.getDefaultMessage(),
                HttpStatus.BAD_REQUEST, "validation");
    }


    private ResponseEntity<VndErrors> error(final String message, final HttpStatus httpStatus, final String logRef) {
        return new ResponseEntity<VndErrors>(new VndErrors(logRef, message), httpStatus);
    }
}
