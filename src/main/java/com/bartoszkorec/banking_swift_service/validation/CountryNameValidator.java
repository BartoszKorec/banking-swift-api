package com.bartoszkorec.banking_swift_service.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import org.springframework.stereotype.Component;

import static com.bartoszkorec.banking_swift_service.util.FieldHelper.validateAndTrim;
import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.logWarning;

@Component
public class CountryNameValidator implements FieldValidator {

    @Override
    public String validate(String fieldValue, int lineNumber) {
        fieldValue = validateAndTrim(fieldValue, true,"Country name");
        if (!fieldValue.matches("^[A-Z]+$")) {
            String errorMessage = "Country name does not meet requirements";
            logWarning(errorMessage, lineNumber);
            throw new InvalidFieldsException(errorMessage);
        }
        return fieldValue;
    }
}