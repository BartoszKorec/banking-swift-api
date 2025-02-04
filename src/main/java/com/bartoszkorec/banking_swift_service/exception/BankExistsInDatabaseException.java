package com.bartoszkorec.banking_swift_service.exception;

public class BankExistsInDatabaseException extends RuntimeException {

    public BankExistsInDatabaseException(String message) {
        super(message);
    }

    public BankExistsInDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BankExistsInDatabaseException(Throwable cause) {
        super(cause);
    }

}
