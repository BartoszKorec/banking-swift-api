package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessorImpl;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
@Slf4j
public class SwiftFileServiceImpl implements SwiftFileService, SmartInitializingSingleton {

    @Value("${swift.file.path}")
    private String swiftFilePath;

    private final BranchService branchService;
    private final HeadquartersService headquartersService;
    private final SwiftDataProcessorImpl processor;
    private final CountryService countryService;

    @Autowired
    public SwiftFileServiceImpl(BranchService branchService, HeadquartersService headquartersService, SwiftDataProcessorImpl processor, CountryService countryService) {
        this.branchService = branchService;
        this.headquartersService = headquartersService;
        this.processor = processor;
        this.countryService = countryService;
    }

    @Override
    public void processSwiftFile(Path path) {

        try (Stream<String> lines = Files.lines(path).skip(1)) { // skip header
            processor.processLines(lines);
        } catch (Exception e) {
            log.error("Error processing SWIFT file: {}. Error: {}", path, e.getMessage());
        }

        processor.getBanks().values().stream()
                .sorted(Comparator.comparing(BankDTO::isHeadquarters).reversed()) // headquarters comes first
                .forEach(this::addDTOToDatabase);
    }

    private void addDTOToDatabase(BankDTO bankDTO) {
//        // No need to validate bankDTO since it's validated during processing lines
        if (bankDTO.isHeadquarters()) {
            headquartersService.addHeadquartersToDatabase((bankDTO));
        } else {
            branchService.addBranchDTOToDatabase(bankDTO);
        }
//        try {
//            if (bankDTO.isHeadquarters()) {
//                headquartersService.addHeadquartersToDatabase((bankDTO));
//            } else {
//                branchService.addBranchDTOToDatabase(bankDTO);
//            }
//        } catch (Exception e) {
//            log.warn("Error processing bank with SWIFT code: {}. Error: {}", bankDTO.getSwiftCode(), e.getMessage());
//        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (countryService.isDatabaseEmpty()) {
            processSwiftFile(Path.of(swiftFilePath));
        }
    }
}
