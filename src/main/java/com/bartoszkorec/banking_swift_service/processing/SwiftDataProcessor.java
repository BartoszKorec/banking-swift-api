package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;

import java.util.Map;
import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.validation.SwiftDataValidator.validateFields;

public interface SwiftDataProcessor {

    void processLines(Stream<String> lines);

    default BankDTO processData(String address, String bankName, String iso2Code, String countryName, String swiftCode, String lineNumber) {

        boolean isHeadquarters = swiftCode.endsWith("XXX");
        if (!validateFields(iso2Code, swiftCode, bankName, address, countryName, isHeadquarters, lineNumber)) {
            return null;
        }
        return new BankDTO(address, bankName, iso2Code, countryName, isHeadquarters, swiftCode);
    }

    default BankDTO processData(String address, String bankName, String iso2Code, String countryName, String swiftCode) {
        return processData(address, bankName, iso2Code, countryName, swiftCode, null);
    }

    Map<String, BankDTO> getBanks();
}
