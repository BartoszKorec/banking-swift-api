package com.bartoszkorec.banking_swift_service.processor;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.validation.FieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;

import static com.bartoszkorec.banking_swift_service.util.FieldValidator.validateAndTrim;

@Component
public class BankDTOProcessor {

    private final FieldValidator countryCodeValidator;
    private final FieldValidator countryNameValidator;
    private final FieldValidator swiftCodeValidator;

    @Autowired
    public BankDTOProcessor(@Qualifier("countryCodeValidator") FieldValidator countryCodeValidator,
                            @Qualifier("countryNameValidator") FieldValidator countryNameValidator,
                            @Qualifier("swiftCodeValidator") FieldValidator swiftCodeValidator) {
        this.countryCodeValidator = countryCodeValidator;
        this.countryNameValidator = countryNameValidator;
        this.swiftCodeValidator = swiftCodeValidator;
    }


    public BankDTO processBankDTO(BankDTO bankDTO, int lineNumber) {

        bankDTO.setCountryISO2(countryCodeValidator.validate(bankDTO.getCountryISO2(), lineNumber));
        bankDTO.setCountryName(countryNameValidator.validate(bankDTO.getCountryName(), lineNumber));
        bankDTO.setBankName(validateAndTrim(bankDTO.getBankName(), false, "Bank name", lineNumber));
        bankDTO.setAddress(validateAndTrim(bankDTO.getAddress(), false, "Address", lineNumber));

        String swiftCode = swiftCodeValidator.validate(bankDTO.getSwiftCode(), lineNumber);
        boolean isHeadquarters = swiftCode.endsWith("XXX");
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

    public BankDTO processBankDTO(BankDTO bankDTO) {
        return processBankDTO(bankDTO, -1);
    }
}
//        if (isHeadquarters) {
//            if (!swiftCode.endsWith("XXX")) {
//                String errorMessage = "Invalid headquarters SWIFT code. A SWIFT code not ending with 'XXX' indicates a branch: " + swiftCode;
//                logWarning(lineNumber, errorMessage);
//                throw new InvalidHeadquartersException(errorMessage);
//            }
//        } else {
//            if (swiftCode.endsWith("XXX")) {
//                String errorMessage = "Invalid branch SWIFT code. A SWIFT code ending with 'XXX' indicates a headquarters: " + swiftCode;
//                logWarning(lineNumber, errorMessage);
//                throw new InvalidBranchException(errorMessage);
//            }
