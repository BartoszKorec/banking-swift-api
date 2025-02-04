package com.bartoszkorec.banking_swift_service.exception;

public class InvalidFieldsException extends RuntimeException {
    public InvalidFieldsException(String message) {
        super(message);
    }

    public InvalidFieldsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFieldsException(Throwable cause) {
        super(cause);
    }
}
