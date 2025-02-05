package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.exception.BankNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.CountryNotFoundException;
import com.bartoszkorec.banking_swift_service.processing.SwiftDataValidatorAndProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwiftServiceImpl implements SwiftService {

    private final HeadquartersService headquartersService;
    private final BranchService branchService;
    private final CountryService countryService;

    @Autowired
    public SwiftServiceImpl(HeadquartersService headquartersService, BranchService branchService, CountryService countryService) {
        this.headquartersService = headquartersService;
        this.branchService = branchService;
        this.countryService = countryService;
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {

        swiftCode = SwiftDataValidatorAndProcessor.safeTrim(swiftCode, true, "SWIFT code is blank or null");
        BankDTO bankDTO;
        try {
            bankDTO = headquartersService.findBySwiftCode(swiftCode);
        } catch (Exception e) {
            // Headquarters not found, try branches
            try {
                bankDTO = branchService.findBySwiftCode(swiftCode);
            } catch (Exception ex) {
                throw new BankNotFoundException("No bank found with SWIFT code: " + swiftCode);
            }
        }
        return bankDTO;
    }

    @Override
    public CountryDTO findByCountryISO2code(String countryISO2code) {
        countryISO2code = SwiftDataValidatorAndProcessor.safeTrim(countryISO2code, true, "Country ISO2 code is blank or null");
        CountryDTO countryDTO;
        try {
            countryDTO = countryService.findByIso2Code(countryISO2code);
        } catch (Exception e) {
            throw new CountryNotFoundException("No country found with ISO2 code: " + countryISO2code);
        }
        return countryDTO;
    }

    @Override
    public void addBankToDatabase(BankDTO bank) {

        String swiftCode = SwiftDataValidatorAndProcessor.safeTrim(bank.getSwiftCode(), true, "SWIFT code is blank or null");
        bank.setHeadquarters(swiftCode.endsWith("XXX"));
        if (bank.isHeadquarters()) {
            headquartersService.addHeadquartersToDatabase(SwiftDataValidatorAndProcessor.processAndValidateBankDTO(bank));
        } else {
            branchService.addBranchDTOToDatabase(SwiftDataValidatorAndProcessor.processAndValidateBankDTO(bank));
        }
    }

    @Override
    public void deleteBank(String swiftCode) {
        swiftCode = SwiftDataValidatorAndProcessor.safeTrim(swiftCode, true, "SWIFT code is blank or null");
        if (swiftCode.endsWith("XXX")) {
            headquartersService.deleteHeadquartersFromDatabase(swiftCode);
        } else {
            branchService.deleteBranchFromDatabase(swiftCode);
        }
    }
}
