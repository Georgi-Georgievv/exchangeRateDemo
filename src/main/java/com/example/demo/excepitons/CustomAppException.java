package com.example.demo.excepitons;

import org.springframework.http.HttpStatus;

public abstract class CustomAppException extends RuntimeException {
    private final String code;

    public CustomAppException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public abstract HttpStatus getStatus();
}
