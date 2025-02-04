package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Getter
@Component
public class SwiftDataProcessorImpl implements SwiftDataProcessor {

    private final Map<String, BankDTO> banks = new HashMap<>();

    @Override
    public void processLines(Stream<String> lines) {

        AtomicInteger lineCounter = new AtomicInteger(2);

        lines.map(line -> line + "\t" + lineCounter.getAndIncrement())
                .map(line -> line.split("\t"))
                .forEach(this::processLine);
    }

    private void processLine(String[] fields) {

        String iso2Code    = fields[FieldIndex.ISO2CODE.getIndex()];
        String swiftCode   = fields[FieldIndex.SWIFT_CODE.getIndex()];
        String bankName    = fields[FieldIndex.BANK_NAME.getIndex()];
        String address     = fields[FieldIndex.ADDRESS.getIndex()];
        String countryName = fields[FieldIndex.COUNTRY.getIndex()];
        String lineNumber  = fields[FieldIndex.LINE_NUMBER.getIndex()];

        try {
            BankDTO bankDTO = processAndValidateData(address, bankName, iso2Code, countryName, swiftCode, lineNumber);
            addBankToMap(bankDTO);
        } catch (IllegalArgumentException ignore) {}
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
}