package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.exception.InvalidBranchException;
import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.exception.InvalidHeadquartersException;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.validation.SwiftDataValidator.validateFields;

public interface SwiftDataProcessor {

    void processLines(Stream<String> lines);

    default BankDTO processAndValidateData(String address, String bankName, String iso2Code, String countryName, String swiftCode, String lineNumber) {

        iso2Code = safeTrimAndUpperCase(iso2Code, "ISO2 code is null");
        countryName = safeTrimAndUpperCase(countryName, "Country name is null");
        swiftCode = safeTrimAndUpperCase(swiftCode, "SWIFT code is null");

        boolean isHeadquarters = swiftCode.endsWith("XXX");
        validateFields(iso2Code, swiftCode, bankName, address, countryName, isHeadquarters, lineNumber);

        address = address.strip();
        bankName = bankName.strip();
        return new BankDTO(address, bankName, iso2Code, countryName, isHeadquarters, swiftCode, null);
    }

    private BankDTO processAndValidateBankDTO(BankDTO bankDTO) {

        validateFields(bankDTO.getCountryISO2(),
                bankDTO.getSwiftCode(),
                bankDTO.getBankName(),
                bankDTO.getAddress(),
                bankDTO.getCountryName(),
                bankDTO.isHeadquarters(),
                null
        );

        bankDTO.setAddress(bankDTO.getAddress().strip());
        bankDTO.setBankName(bankDTO.getBankName().strip());

        return bankDTO;
    }

    default BankDTO processAndValidateHeadquartersDTO(BankDTO headquartersDTO) {

        String swiftCode = headquartersDTO.getSwiftCode();
        if (!swiftCode.endsWith("XXX")) {
            throw new InvalidHeadquartersException("Invalid headquarters SWIFT code. A SWIFT code not ending with 'XXX' indicates a branch: " + swiftCode);
        }

        // currently isHeadquarters field that client pass is omitted
        headquartersDTO.setHeadquarters(true); // to address client isHeadquarters, remove this line
        if (headquartersDTO.getBranches() == null) {
            headquartersDTO.setBranches(new HashSet<>());
        }
        return processAndValidateBankDTO(headquartersDTO);
    }

    default BankDTO processAndValidateBranchDTO(BankDTO branchDTO) {

        String swiftCode = branchDTO.getSwiftCode();
        if (swiftCode.endsWith("XXX")) {
            throw new InvalidBranchException("Invalid branch SWIFT code. A SWIFT code ending with 'XXX' indicates a headquarters: " + swiftCode);
        }

        // currently isHeadquarters field that client pass is omitted
        branchDTO.setHeadquarters(false); // to address client isHeadquarters, remove this line
        branchDTO.setBranches(null);
        return processAndValidateBankDTO(branchDTO);
    }

    Map<String, BankDTO> getBanks();

    default String safeTrimAndUpperCase(String value, String errorMessage) {
        if (value == null) {
            throw new InvalidFieldsException(errorMessage);
        }
        return value.strip().toUpperCase();
    }
}
