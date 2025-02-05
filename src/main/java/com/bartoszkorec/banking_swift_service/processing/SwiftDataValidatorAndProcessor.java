package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.exception.InvalidBranchException;
import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.exception.InvalidHeadquartersException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
public abstract class SwiftDataValidatorAndProcessor {

    public static BankDTO processAndValidateBankDTO(BankDTO bankDTO, String lineNumber) {

        String swiftCode = safeTrim(bankDTO.getSwiftCode(), true, "SWIFT code is blank or null", lineNumber);
        String ISO2Code = safeTrim(bankDTO.getCountryISO2(), true, "ISO2 code is blank or null", lineNumber);
        String countryName = safeTrim(bankDTO.getCountryName(), true, "Country name is blank or null", lineNumber);

        // currently isHeadquarters field that client pass is omitted
        boolean isHeadquarters = swiftCode.endsWith("XXX");

        validateSwiftCode(swiftCode, isHeadquarters, lineNumber);
        validateISO2code(ISO2Code, lineNumber);
        validateCountryName(countryName, lineNumber);

        bankDTO.setSwiftCode(swiftCode);
        bankDTO.setCountryISO2(ISO2Code);
        bankDTO.setCountryName(countryName);
        bankDTO.setBankName(safeTrim(bankDTO.getBankName(), false, "Bank name is blank or null", lineNumber));
        bankDTO.setAddress(safeTrim(bankDTO.getAddress(), false, "Bank's address is blank or null", lineNumber));

        bankDTO.setHeadquarters(isHeadquarters);

        if (isHeadquarters) {
            if (bankDTO.getBranches() == null) {
                bankDTO.setBranches(new HashSet<>());
            }
        } else {
            bankDTO.setBranches(null);
        }

        return bankDTO;
    }

    public static String safeTrim(String value, boolean toUpperCase, String errorMessage) {
        return safeTrim(value, toUpperCase, errorMessage, null);
    }

    public static String safeTrim(String value, boolean toUpperCase, String errorMessage, String lineNumber) {
        if (value == null || value.isBlank()) {
            logWarning(lineNumber, errorMessage);
            throw new InvalidFieldsException(errorMessage);
        }
        return toUpperCase ? value.strip().toUpperCase() : value.strip();
    }

    private static void validateISO2code(String iso2code, String lineNumber) {
        if (!iso2code.matches("^[A-Z]{2}$")) {
            String errorMessage = "ISO2 code doesn't match requirements";
            logWarning(lineNumber, errorMessage);
            throw new InvalidFieldsException(errorMessage);
        }
    }

    private static void validateSwiftCode(String swiftCode, boolean isHeadquarters, String lineNumber) {

        if (!swiftCode.matches("^[A-Z0-9]{11}$")) {
            String errorMessage = "SWIFT code doesn't match requirements";
            logWarning(lineNumber, errorMessage);
            throw new InvalidFieldsException(errorMessage);
        }

        if (isHeadquarters) {
            if (!swiftCode.endsWith("XXX")) {
                String errorMessage = "Invalid headquarters SWIFT code. A SWIFT code not ending with 'XXX' indicates a branch: " + swiftCode;
                logWarning(lineNumber, errorMessage);
                throw new InvalidHeadquartersException(errorMessage);
            }
        } else {
            if (swiftCode.endsWith("XXX")) {
                String errorMessage = "Invalid branch SWIFT code. A SWIFT code ending with 'XXX' indicates a headquarters: " + swiftCode;
                logWarning(lineNumber, errorMessage);
                throw new InvalidBranchException(errorMessage);
            }
        }
    }

    private static void validateCountryName(String countryName, String lineNumber) {
        if (!countryName.matches("[A-Z]+")) {
            String errorMessage = "Country name doesn't match requirements";
            logWarning(lineNumber, errorMessage);
            throw new InvalidFieldsException(errorMessage);
        }
    }

    private static void logWarning(String lineNumber, String errorMessage) {
        if (lineNumber != null) {
            log.warn("Line {}: {}", lineNumber, errorMessage);
        }
    }

    public static BankDTO processAndValidateBankDTO(BankDTO bankDTO) {
        return processAndValidateBankDTO(bankDTO, null);
    }
}