package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.exception.BankNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.CountryNotFoundException;
import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwiftServiceImpl implements SwiftService {

    private final HeadquartersService headquartersService;
    private final BranchService branchService;
    private final CountryService countryService;
    private final SwiftDataProcessor processor;

    @Autowired
    public SwiftServiceImpl(HeadquartersService headquartersService, BranchService branchService, CountryService countryService, SwiftDataProcessor processor) {
        this.headquartersService = headquartersService;
        this.branchService = branchService;
        this.countryService = countryService;
        this.processor = processor;
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {

        swiftCode = processor.safeTrimAndUpperCase(swiftCode, "SWIFT code is null");
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
        countryISO2code = processor.safeTrimAndUpperCase(countryISO2code, "Country ISO2 code is nul");
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

        String swiftCode = processor.safeTrimAndUpperCase(bank.getSwiftCode(), "SWIFT code is null");
        bank.setCountryName(processor.safeTrimAndUpperCase(bank.getCountryName(), "Country name is null is null"));
        bank.setCountryISO2(processor.safeTrimAndUpperCase(bank.getCountryISO2(), "Country ISO2 code is null"));
        bank.setSwiftCode(swiftCode);

        if (swiftCode.endsWith("XXX")) {
            headquartersService.addHeadquartersToDatabase(bank);
        } else {
            branchService.addBranchDTOToDatabase(bank);
        }
    }

    @Override
    public void deleteBank(String swiftCode) {
        swiftCode = processor.safeTrimAndUpperCase(swiftCode, "SWIFT code is null");
        if (swiftCode.endsWith("XXX")) {
            headquartersService.deleteHeadquartersFromDatabase(swiftCode);
        } else {
            branchService.deleteBranchFromDatabase(swiftCode);
        }
    }
}
