package com.bartoszkorec.banking_swift_service.exception;

public class CorrespondingHeadquartersNotFoundException extends RuntimeException {
    public CorrespondingHeadquartersNotFoundException(String message) {
        super(message);
    }
}
