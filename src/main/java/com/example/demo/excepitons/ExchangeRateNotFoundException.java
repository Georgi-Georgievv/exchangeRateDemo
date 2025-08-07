package com.example.demo.excepitons;

import org.springframework.http.HttpStatus;

public class ExchangeRateNotFoundException extends CustomAppException {

    public ExchangeRateNotFoundException(String message, Throwable error) {
        super("EXCHANGE_RATE_NOT_FOUND", message);
        initCause(error);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
