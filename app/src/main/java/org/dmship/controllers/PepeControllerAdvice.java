package org.dmship.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.dmship.exceptions.ResourceConflictException;
import org.dmship.exceptions.ResourceInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

@ControllerAdvice
public class PepeControllerAdvice {

    private static Logger logger = LoggerFactory.getLogger(PepeControllerAdvice.class);

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<String> handleResourceConflictException(ResourceConflictException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceInternalException.class)
    public ResponseEntity<String> handleResourceInternalException(ResourceInternalException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        StringBuilder builder = new StringBuilder();
        if (!violations.isEmpty()) {
            violations.forEach(violation -> {
                List<String> properties = new ArrayList<>();
                violation.getPropertyPath().iterator().forEachRemaining(node -> properties.add(node.toString()));

                builder.append(String.format("param: %s, value: '%s', error: %s\n",
                        violation.getPropertyPath(),
                        violation.getInvalidValue(),
                        violation.getMessage())); });
        }

        logger.info("ConstraintViolation caught, return bad request with body:\n" + builder.toString());
        return new ResponseEntity<>(builder.toString(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions( MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

            builder.append(String.format("param: %s, error: %s\n", fieldName, errorMessage));
        });

        logger.info("MethodArgumentNotValidException caught, return bad request with body:\n" + builder.toString());
        return new ResponseEntity<>(builder.toString(), HttpStatus.BAD_REQUEST);
    }
}


