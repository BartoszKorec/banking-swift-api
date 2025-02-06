package com.bartoszkorec.banking_swift_service.reader;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.processor.BankDTOProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.util.LoggerHelper.logWarning;

@Component
public class TSVRecordsProcessor {

    private final BankDTOProcessor bankDTOProcessor;
    private final Map<String, BankDTO> banks = new HashMap<>();

    @Autowired
    public TSVRecordsProcessor(BankDTOProcessor bankDTOProcessor) {
        this.bankDTOProcessor = bankDTOProcessor;
    }

    public Stream<BankDTO> convertLinesToBankDTOs(List<LineRecord> records) {

        records.forEach(this::convertLineToBankDTO);
        return banks.values().stream();
    }

    private void convertLineToBankDTO(LineRecord record) {

        BankDTO bankDTO;
        try {
            bankDTO = bankDTOProcessor.processBankDTO(new BankDTO(
                    record.fields()[FieldIndex.ADDRESS.getIndex()],
                    record.fields()[FieldIndex.BANK_NAME.getIndex()],
                    record.fields()[FieldIndex.ISO2CODE.getIndex()],
                    record.fields()[FieldIndex.COUNTRY.getIndex()],
                    record.fields()[FieldIndex.SWIFT_CODE.getIndex()].endsWith("XXX"),
                    record.fields()[FieldIndex.SWIFT_CODE.getIndex()],
                    Collections.emptySet()
            ), record.lineNumber());
        } catch (InvalidFieldsException ignore) {
            return;
        }
        storeBankDTO(bankDTO);
    }

    private void storeBankDTO(BankDTO bankDTO) {

        String swiftCode = bankDTO.getSwiftCode();
        if (banks.containsKey(swiftCode)) {
            String duplicateType = bankDTO.isHeadquarters() ? "headquarters" : "branch";
            logWarning("Duplicate " + duplicateType + " found with Swift code: " + swiftCode);
            return;
        }
        banks.put(swiftCode, bankDTO);
    }
}