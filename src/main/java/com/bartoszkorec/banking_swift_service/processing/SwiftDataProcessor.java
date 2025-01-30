package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.api.BranchDTO;
import com.bartoszkorec.banking_swift_service.dto.api.HeadquarterDTO;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Service
//@Slf4j
public class SwiftDataProcessor {

    private final Map<String, HeadquarterDTO> headquarters = new HashMap<>();
    private final Map<String, BranchDTO> branches = new HashMap<>();

    public void processLines(Stream<String> lines) {

        Comparator<String> comparator = new EndsWithXXXComparator();
        int[] lineCounter = {2};

        lines.skip(1)
                .map(line -> line + "\t" + lineCounter[0]++)
                .map(line -> line.split("\t"))
                .sorted((arr1, arr2) -> comparator.compare(arr1[1], arr2[1]))
                .forEach(this::parseLine);
    }

    private void parseLine(String[] arr) {
        String iso2code = arr[0].strip();
        String swiftCode = arr[1].strip();
        String name = arr[3].strip();
        String address = arr[4].strip();
        String countryName = arr[6].strip();
        String lineNumber = arr[8];

        List<String> missingFields = validateRequiredFields(iso2code, swiftCode, name, address, countryName);
        if (!missingFields.isEmpty()) {
//            log.warn("Line {}: Invalid data - missing fields: {}", lineNumber, String.join(", ", missingFields));
            return;
        }

        if (!swiftCode.endsWith("XXX")) {
            processBranch(iso2code, swiftCode, name, address, countryName, lineNumber);
        } else {
            processHeadquarter(iso2code, swiftCode, name, address, countryName, lineNumber);
        }
    }

    private void processHeadquarter(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
        if (headquarters.containsKey(swiftCode)) {
//            log.warn("Line {}: Duplicate headquarter found with Swift code: {}", lineNumber, swiftCode);
            return;
        }
        String swiftPrefix = swiftCode.substring(0, 8);
        var correspondingBranches = branches.entrySet().stream()
                .filter(e -> e.getKey().contains(swiftPrefix))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
        HeadquarterDTO headquarter = new HeadquarterDTO(address, name, iso2code, countryName, swiftCode);
        headquarter.getBranches().addAll(correspondingBranches);
        headquarters.put(swiftCode, headquarter);
    }

    private void processBranch(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {

        if (branches.containsKey(swiftCode)) {
//            log.warn("Line {}: Duplicate branch found with Swift code: {}", lineNumber, swiftCode);
            return;
        }
        branches.put(swiftCode, new BranchDTO(address, name, iso2code, countryName, swiftCode));
    }

    private List<String> validateRequiredFields(String... fields) {
        List<String> missingFields = new ArrayList<>();
        String[] fieldNames = {"COUNTRY ISO2 CODE", "SWIFT CODE", "NAME", "ADDRESS", "COUNTRY NAME"};

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                missingFields.add(fieldNames[i]);
            }
        }
        return missingFields;
    }
}
