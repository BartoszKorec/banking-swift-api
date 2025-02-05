package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.exception.*;
import com.bartoszkorec.banking_swift_service.reader.TSVRecordsProcessor;
import com.bartoszkorec.banking_swift_service.reader.TSVFileReader;
import com.bartoszkorec.banking_swift_service.processor.BankDTOProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Comparator;

import static com.bartoszkorec.banking_swift_service.util.FieldValidator.*;
import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.*;

@Service
public class BankServiceImpl implements BankService {

    @Value("${swift.file.path}")
    private String filePath;
    private final HeadquartersService headquartersService;
    private final BranchService branchService;
    private final CountryService countryService;
    private final BankDTOProcessor bankDTOProcessor;
    private final TSVRecordsProcessor parse;

    @Autowired
    public BankServiceImpl(HeadquartersService headquartersService, BranchService branchService, CountryService countryService, BankDTOProcessor bankDTOProcessor, TSVRecordsProcessor parse) {
        this.headquartersService = headquartersService;
        this.branchService = branchService;
        this.countryService = countryService;
        this.bankDTOProcessor = bankDTOProcessor;
        this.parse = parse;
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

    @Override
    public void afterSingletonsInstantiated() {

        if (countryService.isDatabaseEmpty()) {
            try {
                parse.convertLinesToBankDTOs(TSVFileReader.readTSVFile(filePath))
                        .sorted(Comparator.comparing(BankDTO::isHeadquarters).reversed())
                        .forEach(bankDTO -> {
                            try {
                                addBankToDatabase(bankDTO);
                            } catch (InvalidHeadquartersException | InvalidBranchException |
                                     CorrespondingHeadquartersNotFoundException e) {
                                logWarning(e.getMessage());
                            }
                        });
                logInfo("file " + Path.of(filePath).getFileName() + " processed successfully");
            } catch (FileException e) {
                logError(e.getMessage() + ". Aborting...");
                throw new FileException(e.getMessage());
            }
        }
    }
}
