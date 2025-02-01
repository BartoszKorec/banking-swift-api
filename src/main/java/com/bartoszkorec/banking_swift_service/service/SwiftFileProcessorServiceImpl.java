package com.bartoszkorec.banking_swift_service.service;

import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public class SwiftFileProcessorServiceImpl implements SwiftFileProcessorService {

    @Value("${swift.file.path}")
    private String filePath;
    private final BranchService branchService;
    private final HeadquartersService headquartersService;
    private final LocationService locationService;
    private final CountryService countryService;
    private final SwiftDataProcessor processor;

    @Autowired
    public SwiftFileProcessorServiceImpl(BranchService branchService, HeadquartersService headquartersService, LocationService locationService, CountryService countryService, SwiftDataProcessor processor) {
        this.branchService = branchService;
        this.headquartersService = headquartersService;
        this.locationService = locationService;
        this.countryService = countryService;
        this.processor = processor;
    }

    @Override
    public void processSwiftFile() {

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            processor.processLines(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        processor.getCountries().values().forEach(countryService::processCountry);
        processor.getLocations().values().forEach(locationService::processLocation);
        processor.getHeadquarters().values().forEach(headquartersService::processHeadquarters);
        processor.getBranches().values().forEach(branchService::processBranch);
    }
}
