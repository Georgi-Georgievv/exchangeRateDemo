package com.example.demo.exceptionHandler;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.excepitons.CustomAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptions(CustomAppException ex) {
        log.warn("Handled custom exception: [{}] {}", ex.getCode(), ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred.");
        return ResponseEntity.internalServerError().body(error);
    }
}
