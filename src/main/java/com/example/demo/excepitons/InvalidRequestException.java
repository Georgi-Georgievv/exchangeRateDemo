package com.example.demo.excepitons;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends CustomAppException {

    public InvalidRequestException(String message) {
        super("INVALID_REQUEST", message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
