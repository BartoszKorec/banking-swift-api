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
    private final SwiftDataProcessor swiftDataProcessor;

    @Autowired
    public SwiftServiceImpl(HeadquartersService headquartersService, BranchService branchService, CountryService countryService, SwiftDataProcessor swiftDataProcessor) {
        this.headquartersService = headquartersService;
        this.branchService = branchService;
        this.countryService = countryService;
        this.swiftDataProcessor = swiftDataProcessor;
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {

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

        bank = swiftDataProcessor.processAndValidateBankDTO(bank);
        if (bank.isHeadquarters()) {
            headquartersService.addHeadquartersToDatabase(bank);
        } else {
            branchService.addBranchDTOToDatabase(bank);
        }
    }

    @Override
    public void deleteBank(String swiftCode) {
        if (swiftCode.endsWith("XXX")) {
            headquartersService.deleteHeadquartersFromDatabase(swiftCode);
        } else {
            branchService.deleteBranchFromDatabase(swiftCode);
        }
    }
}
