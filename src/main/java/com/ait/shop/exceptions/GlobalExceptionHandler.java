package com.ait.shop.exceptions;

import com.ait.shop.exceptions.types.EntityNotFoundException;
import com.ait.shop.exceptions.types.EntityUpdateException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException e) {
        String message = e.getMessage();
        logger.warn(message);

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityUpdateException.class)
    public ResponseEntity<String> handleException(EntityUpdateException e) {
        String message = e.getMessage();
        logger.warn(message);

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleException(NullPointerException e) {
        String message = e.getMessage();
        logger.error(message, e);

        return new ResponseEntity<>(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleException(ConstraintViolationException e) {
        List<String> messages = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        messages.forEach(logger::warn);

        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }
}
