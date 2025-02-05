package com.bartoszkorec.banking_swift_service.validation;

public interface FieldValidator {

    String validate(String fieldValue, int lineNumber);

    default String validate(String fieldValue) {
        return validate(fieldValue, -1);
    }
}
