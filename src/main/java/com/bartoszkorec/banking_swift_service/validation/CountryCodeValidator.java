package com.bartoszkorec.banking_swift_service.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import org.springframework.stereotype.Component;

import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.logWarning;
import static com.bartoszkorec.banking_swift_service.util.FieldValidator.validateAndTrim;

@Component
public class CountryCodeValidator implements FieldValidator {

    @Override
    public String validate(String fieldValue, int lineNumber) {
        fieldValue = validateAndTrim(fieldValue, true,"Country code");
        if (!fieldValue.matches("^[A-Z]{2}$")) {
            String errorMessage = "Country code does not meet requirements";
            logWarning(errorMessage, lineNumber);
            throw new InvalidFieldsException(errorMessage);
        }
        return fieldValue;
    }
}