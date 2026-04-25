package com.yourname.tomorrowlandshop.domain.exception;

public class OrderConflictException extends RuntimeException {

    public OrderConflictException(String message) {
        super(message);
    }
}
