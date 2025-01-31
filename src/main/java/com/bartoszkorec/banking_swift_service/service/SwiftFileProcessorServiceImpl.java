package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.config.AppConfig;
import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class SwiftFileProcessorServiceImpl implements SwiftFileProcessorService {

    private final AppConfig appConfig;
    private final BranchService branchService;
    private final HeadquarterService headquarterService;
    private final LocationService locationService;
    private final CountryService countryService;
    private final SwiftDataProcessor processor;

    @Autowired
    public SwiftFileProcessorServiceImpl(AppConfig appConfig, BranchService branchService, HeadquarterService headquarterService, LocationService locationService, CountryService countryService, SwiftDataProcessor processor) {
        this.appConfig = appConfig;
        this.branchService = branchService;
        this.headquarterService = headquarterService;
        this.locationService = locationService;
        this.countryService = countryService;
        this.processor = processor;
    }

    @Override
    public void processSwiftFile() {

        try (Stream<String> lines = Files.lines(Paths.get(appConfig.getFilePath()))) {
            processor.processLines(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        processor.getCountries().values().forEach(countryService::processCountry);
        processor.getLocations().values().forEach(locationService::processLocation);
        processor.getHeadquarters().values().forEach(headquarterService::processHeadquarter);
        processor.getBranches().values().forEach(branchService::processBranch);
    }
}
