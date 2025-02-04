package com.bartoszkorec.banking_swift_service.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class SwiftDataValidator {

    public static void validateFields(String iso2code, String swiftCode, String name, String address, String countryName, boolean isHeadquarters, String lineNumber) {

        List<String> missingFields = checkBlankFields(iso2code, swiftCode, name, address, countryName);
        if (!missingFields.isEmpty()) {
            String message = "Invalid data - missing fields: " + String.join(", ", missingFields);
            if (lineNumber != null) {
                log.warn("Line {}: {}", lineNumber, message);
            }
            throw new InvalidFieldsException(message);
        }

        if (!validateIso2code(iso2code)) {
            String message = "Invalid ISO2code: " + iso2code;
            if (lineNumber != null) {
                log.warn("Line {}: {}", lineNumber, message);
            }
            throw new InvalidFieldsException(message);
        }

        if (!validateSwiftCode(swiftCode, isHeadquarters)) {
            String message = "Invalid SWIFT code: " + swiftCode;
            if (lineNumber != null) {
                log.warn("Line {}: {}", lineNumber, message);
            }
            throw new InvalidFieldsException(message);
        }

        if (!validateCountryName(countryName)) {
            String message = "Invalid COUNTRY_NAME: " + countryName;
            if (lineNumber != null) {
                log.warn("Line {}: {}", lineNumber, message);
            }
            throw new InvalidFieldsException(message);
        }
    }

    public static void validateFields(String iso2code, String swiftCode, String name, String address, String countryName, boolean isHeadquarters) {
        validateFields(iso2code, swiftCode, name, address, countryName, isHeadquarters, null);
    }

    private static List<String> checkBlankFields(String... fields) {
        String[] fieldNames = {"COUNTRY ISO2 CODE", "SWIFT CODE", "NAME", "ADDRESS", "COUNTRY NAME"};
        List<String> missingFields = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null) {
                missingFields.add(fieldNames[i]);
            } else if (fields[i].isBlank()) {
                missingFields.add(fieldNames[i]);
            }
        }
        return missingFields;
    }

    private static boolean validateIso2code(String iso2code) {
        return iso2code.matches("^[A-Z]{2}$");
    }

    private static boolean validateSwiftCode(String swiftCode, boolean isHeadquarters) {
        return isHeadquarters ? swiftCode.matches("^[A-Z0-9]{8}X{3}$") : swiftCode.matches("^[A-Z0-9]{8}(?!XXX$)[A-Z0-9]{3}$");
    }

    private static boolean validateCountryName(String countryName) {
        return countryName.matches("[A-Z]+");
    }
}
