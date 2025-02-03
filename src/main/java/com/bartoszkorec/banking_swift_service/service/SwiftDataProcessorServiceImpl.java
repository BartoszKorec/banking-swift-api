package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
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
public class SwiftDataProcessorServiceImpl implements SwiftDataProcessorService, SmartInitializingSingleton {

    @Value("${swift.file.path}")
    private String swiftFilePath;

    private final BranchService branchService;
    private final HeadquartersService headquartersService;
    private final SwiftDataProcessorImpl processor;
    private final BankMapper bankMapper;
    private final CountryService countryService;

    @Autowired
    public SwiftDataProcessorServiceImpl(BranchService branchService, HeadquartersService headquartersService, SwiftDataProcessorImpl processor, BankMapper bankMapper, CountryService countryService) {
        this.branchService = branchService;
        this.headquartersService = headquartersService;
        this.processor = processor;
        this.bankMapper = bankMapper;
        this.countryService = countryService;
    }

    @Override
    public void processSwiftFile(Path path) {

        try (Stream<String> lines = Files.lines(path).skip(1)) { // skip header
            processor.processLines(lines);
        } catch (IOException e) {
            log.error("Error processing SWIFT file: {}", path, e);
        }

        processor.getBanks().values().stream()
                .sorted(Comparator.comparing(BankDTO::isHeadquarters).reversed()) // headquarters comes first
                .forEach(this::processDTO);
    }

    @Override
    public void processDTO(BankDTO bankDTO) {
        try {
            if (bankDTO.isHeadquarters()) {
                headquartersService.processHeadquarters(bankMapper.toHeadquartersEntity(bankDTO));
            } else {
                branchService.processBranch(bankMapper.toBranchEntity(bankDTO));
            }
        } catch (NoResultException e) {
            log.warn("Error processing bank with SWIFT code: {}. Error: {}", bankDTO.getSwiftCode(), e.getMessage());
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (countryService.isDatabaseEmpty()) {
            processSwiftFile(Path.of(swiftFilePath));
        }
    }
}
