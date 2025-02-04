package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.validation.SwiftDataValidator.validateFields;

public interface SwiftDataProcessor {

    void processLines(Stream<String> lines);

    default BankDTO processAndValidateData(String address, String bankName, String iso2Code, String countryName, String swiftCode, String lineNumber) {

        iso2Code   = safeTrimAndUpperCase(iso2Code, "ISO2 code is null");
        countryName = safeTrimAndUpperCase(countryName, "Country name is null");
        swiftCode   = safeTrimAndUpperCase(swiftCode, "SWIFT code is null");

        boolean isHeadquarters = swiftCode.endsWith("XXX");
        validateFields(iso2Code, swiftCode, bankName, address, countryName, isHeadquarters, lineNumber);

        address = address.strip();
        bankName = bankName.strip();
        return new BankDTO(address, bankName, iso2Code, countryName, isHeadquarters, swiftCode, new HashSet<>());
    }

    default BankDTO processAndValidateBankDTO(BankDTO bankDTO) {

        bankDTO.setCountryISO2(safeTrimAndUpperCase(bankDTO.getCountryISO2(), "ISO2 code is null"));
        bankDTO.setCountryName(safeTrimAndUpperCase(bankDTO.getCountryName(), "Country name is null"));
        bankDTO.setSwiftCode(safeTrimAndUpperCase(bankDTO.getSwiftCode(), "SWIFT code is null"));

        // currently isHeadquarters field that client pass is omitted
        bankDTO.setHeadquarters(bankDTO.getSwiftCode().endsWith("XXX")); // to address client isHeadquarters, remove this line

        validateFields(bankDTO.getCountryISO2(), bankDTO.getSwiftCode(), bankDTO.getBankName(), bankDTO.getAddress(), bankDTO.getCountryName(), bankDTO.isHeadquarters(), null);

        bankDTO.setAddress(bankDTO.getAddress().strip());
        bankDTO.setBankName(bankDTO.getBankName().strip());

        if (bankDTO.isHeadquarters() && bankDTO.getBranches() == null) {
            bankDTO.setBranches(new HashSet<>());
        } else if (!bankDTO.isHeadquarters()) {
            bankDTO.setBranches(null);
        }

        return bankDTO;
    }

    private String safeTrimAndUpperCase(String value, String errorMessage) {
        if (value == null) {
            throw new InvalidFieldsException(errorMessage);
        }
        return value.strip().toUpperCase();
    }

    Map<String, BankDTO> getBanks();
}
