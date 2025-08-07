package com.example.demo.excepitons;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends CustomAppException {

    public ExternalServiceException(String message) {
        super("EXTERNAL_SERVICE_ERROR", message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_GATEWAY;
    }
}
