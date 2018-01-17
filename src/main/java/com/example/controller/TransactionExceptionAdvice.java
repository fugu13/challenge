package com.example.controller;

import com.example.data.exceptions.SupplierMissingViolation;
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
 * Translate DAO exceptions to HTTP responses for the TransactionController
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(assignableTypes = TransactionController.class)
@RequestMapping(produces = "application/vnd.error+json")
public class TransactionExceptionAdvice {

    /**
     * Attempting to create a transaction with a dangling supplier is a bad request
     *
     * @param e
     * @return bad request
     */
    @ExceptionHandler(SupplierMissingViolation.class)
    public ResponseEntity<VndErrors> missingSupplierException(final SupplierMissingViolation e) {
        return error(e.getMessage(), HttpStatus.BAD_REQUEST, "missing");
    }

    /**
     * Sending a transaction with validation errors is a bad request
     * <p>
     * TODO: move this and the parallel for suppliers to a universal exception advice
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
