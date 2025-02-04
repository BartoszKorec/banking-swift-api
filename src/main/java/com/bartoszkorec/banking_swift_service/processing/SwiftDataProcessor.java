package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.validation.SwiftDataValidator.validateFields;

public interface SwiftDataProcessor {

    void processLines(Stream<String> lines);

    default BankDTO processAndValidateData(String address, String bankName, String iso2Code, String countryName, String swiftCode, String lineNumber) {

        address = address.strip().toUpperCase();
        bankName = bankName.strip().toUpperCase();
        iso2Code = iso2Code.strip().toUpperCase();
        countryName = countryName.strip().toUpperCase();
        swiftCode = swiftCode.strip().toUpperCase();

        boolean isHeadquarters = swiftCode.endsWith("XXX");
        validateFields(iso2Code, swiftCode, bankName, address, countryName, isHeadquarters, lineNumber);
        return new BankDTO(address, bankName, iso2Code, countryName, isHeadquarters, swiftCode, isHeadquarters ? new HashSet<>() : null);
    }

    default BankDTO processAndValidateBankDTO(BankDTO bankDTO) {
        return processAndValidateData(
                bankDTO.getAddress(),
                bankDTO.getBankName(),
                bankDTO.getCountryISO2(),
                bankDTO.getCountryName(),
                bankDTO.getSwiftCode(),
                null
        );
    }

    Map<String, BankDTO> getBanks();
}
