package com.bartoszkorec.banking_swift_service.exception;

public class InvalidFieldsException extends RuntimeException {
    public InvalidFieldsException(String message) {
        super(message);
    }
}
