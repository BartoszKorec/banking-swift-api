package com.bartoszkorec.banking_swift_service.util;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;

import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.logWarning;

public abstract class FieldValidator {

    public static String validateAndTrim(String value, boolean toUpperCase, String fieldName) {
        return validateAndTrim(value, toUpperCase, fieldName, -1);
    }

    public static String validateAndTrim(String value, boolean toUpperCase, String fieldName, int lineNumber) {
        if (value == null || value.isBlank()) {
            String errorMessage = fieldName + " is blank or null";
            logWarning(errorMessage, lineNumber);
            throw new InvalidFieldsException(errorMessage);
        }
        return toUpperCase ? value.strip().toUpperCase() : value.strip();
    }
}
