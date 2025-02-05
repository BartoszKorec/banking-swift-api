package com.bartoszkorec.banking_swift_service.exception;

public class BankExistsInDatabaseException extends RuntimeException {

    public BankExistsInDatabaseException(String message) {
        super(message);
    }
}
