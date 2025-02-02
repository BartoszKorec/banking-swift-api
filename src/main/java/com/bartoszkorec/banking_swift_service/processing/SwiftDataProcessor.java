package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.validation.SwiftDataValidator.validateFields;

@Slf4j
@Getter
@Component
public class SwiftDataProcessor {

    private final Map<String, BankDTO> banks = new HashMap<>();

    public void processLines(Stream<String> lines) {
        int[] lineCounter = {2};

        lines.map(line -> line + "\t" + lineCounter[0]++)
                .skip(1)
                .map(line -> line.split("\t"))
                .forEach(this::processLine);
    }

    private void processLine(String[] arr) {

        String iso2code = arr[0].strip();
        String swiftCode = arr[1].strip();
        String name = arr[3].strip();
        String address = arr[4].strip();
        String countryName = arr[6].strip();
        String lineNumber = arr[8];

        if (!validateFields(iso2code, swiftCode, name, address, countryName, lineNumber)) {
            return;
        }

        boolean isHeadquarter = swiftCode.endsWith("XXX");
        if (banks.containsKey(swiftCode)) {
            if (isHeadquarter) {
                log.warn("Line {}: Duplicate headquarters found with Swift code: {}", lineNumber, swiftCode);
            } else {
                log.warn("Line {}: Duplicate branch found with Swift code: {}", lineNumber, swiftCode);
            }
            return;
        }
        banks.put(swiftCode, new BankDTO(address, name, iso2code, countryName, isHeadquarter, swiftCode));
    }
}