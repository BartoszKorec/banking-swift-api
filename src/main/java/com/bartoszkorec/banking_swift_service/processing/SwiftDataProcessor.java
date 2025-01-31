package com.bartoszkorec.banking_swift_service.processing;


import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Country;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Component
//@Slf4j
public class SwiftDataProcessor {

    private final Map<String, Country> countries = new HashMap<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Headquarters> headquarters = new HashMap<>();
    private final Map<String, Branch> branches = new HashMap<>();

    public void processLines(Stream<String> lines) {

        Comparator<String> comparator = (s1, s2) -> {
            boolean s1EndsWithXXX = s1.endsWith("XXX");
            boolean s2EndsWithXXX = s2.endsWith("XXX");

            if (s1EndsWithXXX && !s2EndsWithXXX) {
                return -1;
            } else if (!s1EndsWithXXX && s2EndsWithXXX) {
                return 1;
            } else {
                return s1.compareTo(s2);
            }
        };
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
//            log.warn("Line {}: Invalid data - missing fields: {}", lineNumber, String.join(", ", missingFields));
            return;
        }

        if (swiftCode.endsWith("XXX")) {
            processHeadquarters(iso2code, swiftCode, name, address, countryName, lineNumber);
        } else {
            processBranch(iso2code, swiftCode, name, address, countryName, lineNumber);
        }
    }

    private void processHeadquarters(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
        if (this.headquarters.containsKey(swiftCode)) {
//            log.warn("Line {}: Duplicate headquarters found with Swift code: {}", lineNumber, swiftCode);
            return;
        }
        Location location = findOrCreateLocation(iso2code, address, countryName);
        Headquarters headquarters = new Headquarters();
        headquarters.setLocation(location);
        headquarters.setSwiftCode(swiftCode);
        headquarters.setName(name);
        this.headquarters.put(swiftCode, headquarters);
    }

    private void processBranch(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
        String headquartersSwiftCode = swiftCode.substring(0, 8) + "XXX";
        Headquarters headquarters = this.headquarters.get(headquartersSwiftCode);

        if (headquarters == null) {
//            log.warn("Line {}: No matching headquarters found for branch with Swift code: {}", lineNumber, swiftCode);
            return;
        }

        if (branches.containsKey(swiftCode)) {
//            log.warn("Line {}: Duplicate branch found with Swift code: {}", lineNumber, swiftCode);
            return;
        }

        Location location = findOrCreateLocation(iso2code, address, countryName);
        Branch branch = new Branch();
        branch.setSwiftCode(swiftCode);
        branch.setName(name);
        branch.setHeadquarters(headquarters);
        branch.setLocation(location);
        branches.put(swiftCode, branch);
    }

    private Location findOrCreateLocation(String iso2code, String address, String countryName) {
        return locations.computeIfAbsent(address, k -> {
            Location location = new Location();
            location.setAddressLine(address);
            location.setCountry(findOrCreateCountry(iso2code, countryName));
            return location;
        });
    }

    private Country findOrCreateCountry(String iso2code, String countryName) {
        return countries.computeIfAbsent(iso2code, k -> {
            Country country = new Country();
            country.setIso2Code(iso2code);
            country.setCountryName(countryName);
            return country;
        });
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