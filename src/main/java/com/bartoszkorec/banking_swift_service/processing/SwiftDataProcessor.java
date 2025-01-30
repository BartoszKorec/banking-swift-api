package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Country;
import com.bartoszkorec.banking_swift_service.entity.Headquarter;
import com.bartoszkorec.banking_swift_service.entity.Location;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Service
@Slf4j
public class SwiftDataProcessor {

    private final Map<String, Country> countries = new HashMap<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Headquarter> headquarters = new HashMap<>();
    private final Map<String, Branch> branches = new HashMap<>();

    public void processLines(Stream<String> lines) {
        Comparator<String> comparator = new EndsWithXXXComparator();
        int[] lineCounter = {2};

        lines.map(line -> line + "\t" + lineCounter[0]++)
                .map(line -> line.split("\t"))
                .sorted((arr1, arr2) -> comparator.compare(arr1[1], arr2[1]))
                .forEach(this::processLine);
    }

    private void processLine(String[] arr) {
        String iso2code = arr[0].strip();
        String swiftCode = arr[1].strip();
        String name = arr[3].strip();
        String address = arr[4].strip();
        String countryName = arr[6].strip();
        String lineNumber = arr[8];

        List<String> missingFields = checkBlankFields(iso2code, swiftCode, name, address, countryName);
        if (!missingFields.isEmpty()) {
            log.warn("Line {}: Invalid data - missing fields: {}", lineNumber, String.join(", ", missingFields));
            return;
        }

        if (swiftCode.endsWith("XXX")) {
            processHeadquarter(iso2code, swiftCode, name, address, countryName, lineNumber);
        } else {
            processBranch(iso2code, swiftCode, name, address, countryName, lineNumber);
        }
    }

    private void processHeadquarter(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
        if (headquarters.containsKey(swiftCode)) {
            log.warn("Line {}: Duplicate headquarter found with Swift code: {}", lineNumber, swiftCode);
            return;
        }
        Location location = findOrCreateLocation(iso2code, address, countryName);
        headquarters.put(swiftCode, new Headquarter(swiftCode, name, location));
    }

    private void processBranch(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
        String headquarterSwiftCode = swiftCode.substring(0, 8) + "XXX";
        Headquarter headquarter = headquarters.get(headquarterSwiftCode);

        if (headquarter == null) {
            log.warn("Line {}: No matching headquarter found for branch with Swift code: {}", lineNumber, swiftCode);
            return;
        }

        if (branches.containsKey(swiftCode)) {
            log.warn("Line {}: Duplicate branch found with Swift code: {}", lineNumber, swiftCode);
            return;
        }

        Location location = findOrCreateLocation(iso2code, address, countryName);
        branches.put(swiftCode, new Branch(swiftCode, name, location, headquarter));
    }

    private Location findOrCreateLocation(String iso2code, String address, String countryName) {
        return locations.computeIfAbsent(address, k -> new Location(address, findOrCreateCountry(iso2code, countryName)));
    }

    private Country findOrCreateCountry(String iso2code, String countryName) {
        return countries.computeIfAbsent(iso2code, k -> new Country(iso2code, countryName));
    }

    private List<String> checkBlankFields(String... fields) {
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
