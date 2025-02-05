package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.service.BranchService;
import com.bartoszkorec.banking_swift_service.service.CountryService;
import com.bartoszkorec.banking_swift_service.service.HeadquartersService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.processing.SwiftDataValidatorAndProcessor.processAndValidateBankDTO;

@Slf4j
@Getter
@Component
class SwiftFileProcessor implements SmartInitializingSingleton {

    private final BranchService branchService;
    private final HeadquartersService headquartersService;
    private final CountryService countryService;
    private final Map<String, BankDTO> banks = new HashMap<>();

    @Value("${swift.file.path}")
    private String swiftFilePath;

    @Autowired
    public SwiftFileProcessor(BranchService branchService, HeadquartersService headquartersService, CountryService countryService) {
        this.branchService = branchService;
        this.headquartersService = headquartersService;
        this.countryService = countryService;
    }

    private void processSwiftFile(Path path) {

        AtomicInteger lineCounter = new AtomicInteger(2);
        try (Stream<String> lines = Files.lines(path).skip(1)) { // skip header
            lines.map(line -> line + "\t" + lineCounter.getAndIncrement())
                    .map(line -> line.split("\t"))
                    .forEach(this::processLine);
        } catch (InvalidFieldsException ignore) {}
        catch (Exception e) {
            log.error("Unexpected error processing SWIFT file: {}. Error: {}", path.toString(), e.getMessage());
        }
    }


    private void processLine(String[] fields) {

        BankDTO bankDTO = new BankDTO();

        bankDTO.setCountryISO2(fields[FieldIndex.ISO2CODE.getIndex()]);
        bankDTO.setSwiftCode(fields[FieldIndex.SWIFT_CODE.getIndex()]);
        bankDTO.setBankName(fields[FieldIndex.BANK_NAME.getIndex()]);
        bankDTO.setAddress(fields[FieldIndex.ADDRESS.getIndex()]);
        bankDTO.setCountryName(fields[FieldIndex.COUNTRY.getIndex()]);
        String lineNumber = fields[FieldIndex.LINE_NUMBER.getIndex()];

        try {
            addBankToMap(processAndValidateBankDTO(bankDTO, lineNumber));
        } catch (InvalidFieldsException ignore) {}
    }

    private void addBankToMap(BankDTO bank) {
        String swiftCode = bank.getSwiftCode();
        if (banks.containsKey(swiftCode)) {
            String duplicateType = swiftCode.endsWith("XXX") ? "headquarters" : "branch";
            log.warn("Duplicate {} found with Swift code: {}", duplicateType, swiftCode);
            return;
        }
        banks.put(swiftCode, bank);
    }

    private void addDTOsToDatabase(BankDTO bankDTO) {
        try {
            if (bankDTO.isHeadquarters()) {
                headquartersService.addHeadquartersToDatabase(bankDTO);
            } else {
                branchService.addBranchDTOToDatabase(bankDTO);
            }
        } catch (RuntimeException e) {
            log.warn("Failed to add BankDTO to database. Error: {}", e.getMessage());
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (countryService.isDatabaseEmpty()) {
            processSwiftFile(Path.of(swiftFilePath));
            banks.values().stream()
                    .sorted(Comparator.comparing(BankDTO::isHeadquarters).reversed()) // headquarters comes first
                    .forEach(this::addDTOsToDatabase);
        }
    }
}