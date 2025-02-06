package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.exception.BankNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.CountryNotFoundException;
import com.bartoszkorec.banking_swift_service.processor.BankDTOProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bartoszkorec.banking_swift_service.util.FieldHelper.validateAndTrim;

@Service
public class BankServiceImpl implements BankService {


    private final HeadquartersService headquartersService;
    private final BranchService branchService;
    private final CountryService countryService;
    private final BankDTOProcessor bankDTOProcessor;

    @Autowired
    public BankServiceImpl(HeadquartersService headquartersService, BranchService branchService, CountryService countryService, BankDTOProcessor bankDTOProcessor) {
        this.headquartersService = headquartersService;
        this.branchService = branchService;
        this.countryService = countryService;
        this.bankDTOProcessor = bankDTOProcessor;
    }

    @Override
    public BankDTO findBySwiftCode(String swiftCode) {

        swiftCode = validateAndTrim(swiftCode, true, "SWIFT code");
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
        countryISO2code = validateAndTrim(countryISO2code, true, "Country ISO2 code");
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

        BankDTO validBank = bankDTOProcessor.processBankDTO(bank);
        if (validBank.isHeadquarters()) {
            headquartersService.addHeadquartersToDatabase(validBank);
        } else {
            branchService.addBranchDTOToDatabase(validBank);
        }
    }

    @Override
    public void deleteBank(String swiftCode) {
        swiftCode = validateAndTrim(swiftCode, true, "SWIFT code");
        if (swiftCode.endsWith("XXX")) {
            headquartersService.deleteHeadquartersFromDatabase(swiftCode);
        } else {
            branchService.deleteBranchFromDatabase(swiftCode);
        }
    }
}
