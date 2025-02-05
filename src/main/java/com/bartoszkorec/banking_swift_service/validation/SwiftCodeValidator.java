package com.bartoszkorec.banking_swift_service.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import org.springframework.stereotype.Component;

import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.logWarning;
import static com.bartoszkorec.banking_swift_service.util.FieldValidator.validateAndTrim;

@Component
public class SwiftCodeValidator implements FieldValidator {

    @Override
    public String validate(String fieldValue, int lineNumber) {
        fieldValue = validateAndTrim(fieldValue, true,"SWIFT code", lineNumber);
        if (!fieldValue.matches("^[A-Z0-9]{11}$")) {
            String errorMessage = "SWIFT code doesn't match requirements";
            logWarning(errorMessage, lineNumber);
            throw new InvalidFieldsException(errorMessage);
        }
        return fieldValue;
    }
}
