package com.bartoszkorec.banking_swift_service.validation;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class SwiftDataValidator {

    public static boolean validateFields(String iso2code, String swiftCode, String name, String address, String countryName, boolean isHeadquarters, String lineNumber) {

        List<String> missingFields = checkBlankFields(iso2code, swiftCode, name, address, countryName);
        if (!missingFields.isEmpty()) {
            if (lineNumber != null) {
                log.warn("Line {}: Invalid data - missing fields: {}", lineNumber, String.join(", ", missingFields));
            }
            return false;
        }

        if (!validateIso2code(iso2code)) {
            if (lineNumber != null) {
                log.warn("Line {}: Invalid ISO2 code: {}", lineNumber, iso2code);
            }
            return false;
        }

        if (!validateSwiftCode(swiftCode, isHeadquarters)) {
            if (lineNumber != null) {
                log.warn("Line {}: Invalid SWIFT code: {}", lineNumber, swiftCode);
            }
            return false;
        }

        if (!validateCountryName(countryName)) {
            if (lineNumber != null) {
                log.warn("Line {}: Invalid COUNTRY_NAME code: {}", lineNumber, countryName);
            }
            return false;
        }

        return true;
    }

    public static boolean validateFields(String iso2code, String swiftCode, String name, String address, String countryName, boolean isHeadquarters) {

        return validateFields(iso2code, swiftCode, name, address, countryName, isHeadquarters, null);
    }

    private static List<String> checkBlankFields(String... fields) {
        String[] fieldNames = {"COUNTRY ISO2 CODE", "SWIFT CODE", "NAME", "ADDRESS", "COUNTRY NAME"};
        List<String> missingFields = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                missingFields.add(fieldNames[i]);
            }
        }
        return missingFields;
    }

    private static boolean validateIso2code(String iso2code) {
        return iso2code.matches("^[A-Z]{2}$");
    }

    private static boolean validateSwiftCode(String swiftCode, boolean isHeadquarters) {
        return isHeadquarters ? swiftCode.matches("^[A-Z0-9]{8}[X]{3}$") : swiftCode.matches("^[A-Z0-9]{8}(?!XXX$)[A-Z0-9]{3}$");
    }

    private static boolean validateCountryName(String countryName) {
        return countryName.matches("[A-Z]+");
    }
}
