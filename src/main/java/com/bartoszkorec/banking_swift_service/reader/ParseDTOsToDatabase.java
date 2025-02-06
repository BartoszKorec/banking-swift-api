package com.bartoszkorec.banking_swift_service.reader;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.exception.InvalidHeadquartersException;
import com.bartoszkorec.banking_swift_service.processor.EntityProcessor;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import com.bartoszkorec.banking_swift_service.service.CountryService;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.logInfo;
import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.logWarning;

@Component
public class ParseDTOsToDatabase implements SmartInitializingSingleton {

    @Value("${swift.file.path}")
    private String filePath;
    private final TSVRecordsProcessor parse;
    private final Map<String, Headquarters> headquartersMap = new HashMap<>();
    private final CountryService countryService;
    private final HeadquartersRepository headquartersRepository;
    private final EntityProcessor entityProcessor;

    @Autowired
    public ParseDTOsToDatabase(TSVRecordsProcessor parse, CountryService countryService, HeadquartersRepository headquartersRepository, EntityProcessor entityProcessor) {
        this.parse = parse;
        this.countryService = countryService;
        this.headquartersRepository = headquartersRepository;
        this.entityProcessor = entityProcessor;
    }

    @Override
    public void afterSingletonsInstantiated() {

        if (countryService.isDatabaseEmpty()) {

            parse.convertLinesToBankDTOs(TSVFileReader.readTSVFile(filePath))
                    .sorted(Comparator.comparing(BankDTO::isHeadquarters).reversed())
                    .forEach(bankDTO -> {
                        if (bankDTO.isHeadquarters()) {
                            Headquarters hq = entityProcessor.processHeadquarters(bankDTO);
                            headquartersMap.put(hq.getSwiftCode(), hq);
                        } else {
                            Branch branch = entityProcessor.processBranch(bankDTO);
                            String hqSwiftCode = branch.getHeadquarters().getSwiftCode();
                            if (headquartersMap.containsKey(hqSwiftCode)) {
                                Headquarters hq = headquartersMap.get(hqSwiftCode);
                                branch.setHeadquarters(hq);
                                headquartersMap.merge(hqSwiftCode, hq, (existingHq, newHq) -> {
                                    existingHq.getBranches().add(branch);
                                    return existingHq;
                                });
                            } else {
                                logWarning("No corresponding headquarters found for the branch with SWIFT code: " + branch.getSwiftCode());
                            }
                        }
                    });
            try {
                headquartersRepository.saveAll(headquartersMap.values());
            } catch (RuntimeException e) {
                throw new InvalidHeadquartersException("Error saving headquarters. Exception: " + e.getClass().getSimpleName() + ". Message: " + e.getMessage());
            }
            logInfo("file '" + Path.of(filePath).getFileName() + "' processed successfully");
        }
    }
}