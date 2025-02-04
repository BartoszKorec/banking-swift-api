package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.exception.BankExistsInDatabaseException;
import com.bartoszkorec.banking_swift_service.exception.BankNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.CorrespondingHeadquartersNotFoundException;
import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
@Slf4j
public class SwiftFileService implements SmartInitializingSingleton {

    @Value("${swift.file.path}")
    private String swiftFilePath;

    private final BranchService branchService;
    private final HeadquartersService headquartersService;
    private final SwiftDataProcessorImpl processor;
    private final CountryService countryService;

    @Autowired
    public SwiftFileService(BranchService branchService, HeadquartersService headquartersService, SwiftDataProcessorImpl processor, CountryService countryService) {
        this.branchService = branchService;
        this.headquartersService = headquartersService;
        this.processor = processor;
        this.countryService = countryService;
    }

    public void processSwiftFile(Path path) {

        try (Stream<String> lines = Files.lines(path).skip(1)) { // skip header
            processor.processLines(lines);
        } catch (InvalidFieldsException e) {
            log.warn("Error processing SWIFT file: {}. Error: {}", path, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing SWIFT file: {}. Error: {}", path, e.getMessage());
        }

        processor.getBanks().values().stream()
                .sorted(Comparator.comparing(BankDTO::isHeadquarters).reversed()) // headquarters comes first
                .forEach(this::addDTOToDatabase);
    }

    private void addDTOToDatabase(BankDTO bankDTO) {
//        // No need to validate bankDTO since it's validated during processing lines
        try {
            if (bankDTO.isHeadquarters()) {
                headquartersService.addHeadquartersToDatabase((bankDTO));
            } else {
                branchService.addBranchDTOToDatabase(bankDTO);
            }
        } catch (CorrespondingHeadquartersNotFoundException e) {
            log.warn("No corresponding headquarters found for the branch with SWIFT code: {}. Error: {}", bankDTO.getSwiftCode(), e.getMessage());
        } catch (BankNotFoundException e) {
            log.warn("Bank not found for the SWIFT code: {}. Error: {}", bankDTO.getSwiftCode(), e.getMessage());
        } catch (BankExistsInDatabaseException e) {
            log.warn("Bank already exists in the database for the SWIFT code: {}. Error: {}", bankDTO.getSwiftCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing bank with SWIFT code: {}. Error: {}", bankDTO.getSwiftCode(), e.getMessage());
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (countryService.isDatabaseEmpty()) {
            processSwiftFile(Path.of(swiftFilePath));
        }
    }
}
