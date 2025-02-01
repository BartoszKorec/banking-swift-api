package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Country;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

//@Slf4j
@Getter
@Component
public class SwiftDataProcessor {

    private final Map<String, Country> countries = new HashMap<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Headquarters> headquarters = new HashMap<>();
    private final Map<String, Branch> branches = new HashMap<>();

    public void processLines(Stream<String> lines) {
        int[] lineCounter = {2};

        lines.map(line -> line + "\t" + lineCounter[0]++)
                .map(line -> line.split("\t"))
                .sorted(Comparator.comparing(arr -> arr[1], this::compareSwiftCodes))
                .forEach(this::processLine);
    }

    private int compareSwiftCodes(String s1, String s2) {
        boolean s1EndsWithXXX = s1.endsWith("XXX");
        boolean s2EndsWithXXX = s2.endsWith("XXX");
        return Boolean.compare(s2EndsWithXXX, s1EndsWithXXX); // Ensures "XXX" codes come first
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

        if (swiftCode.endsWith("XXX")) {
            processHeadquarters(iso2code, swiftCode, name, address, countryName, lineNumber);
        } else {
            processBranch(iso2code, swiftCode, name, address, countryName, lineNumber);
        }
    }

    private boolean validateFields(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
        List<String> missingFields = checkBlankFields(iso2code, swiftCode, name, address, countryName);
        if (!missingFields.isEmpty()) {
            System.err.println("Line " + lineNumber + ": Invalid data - missing fields: " + missingFields);
            // log.warn("Line {}: Invalid data - missing fields: {}", lineNumber, String.join(", ", missingFields));
            return false;
        }

        if (!validateIso2code(iso2code)) {
            System.err.println("Line " + lineNumber + ": Invalid ISO2 code: " + iso2code);
            return false;
        }

        if (!validateSwiftCode(swiftCode)) {
            System.err.println("Line " + lineNumber + ": Invalid SWIFT code: " + swiftCode);
            return false;
        }

        return true;
    }

    private void processHeadquarters(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
        if (headquarters.containsKey(swiftCode)) {
            // log.warn("Line {}: Duplicate headquarters found with Swift code: {}", lineNumber, swiftCode);
            return;
        }
        headquarters.put(swiftCode, new Headquarters(swiftCode, name, findOrCreateLocation(iso2code, address, countryName)));
    }

    private void processBranch(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
        String headquartersSwiftCode = swiftCode.substring(0, 8) + "XXX";
        Headquarters hq = headquarters.get(headquartersSwiftCode);

        if (hq == null || branches.containsKey(swiftCode)) {
            // log.warn("Line {}: No matching headquarters found or duplicate branch with Swift code: {}", lineNumber, swiftCode);
            System.err.println("Line " + lineNumber + ": No matching headquarters found or duplicate branch with Swift code: " + swiftCode);
            return;
        }

        branches.put(swiftCode, new Branch(swiftCode, name, hq, findOrCreateLocation(iso2code, address, countryName)));
    }

    private Location findOrCreateLocation(String iso2code, String address, String countryName) {
        return locations.computeIfAbsent(address, k -> new Location(null, address, findOrCreateCountry(iso2code, countryName)));
    }

    private Country findOrCreateCountry(String iso2code, String countryName) {
        return countries.computeIfAbsent(iso2code, k -> new Country(iso2code, countryName));
    }

    private List<String> checkBlankFields(String... fields) {
        String[] fieldNames = {"COUNTRY ISO2 CODE", "SWIFT CODE", "NAME", "ADDRESS", "COUNTRY NAME"};
        List<String> missingFields = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                missingFields.add(fieldNames[i]);
            }
        }
        return missingFields;
    }

    private boolean validateIso2code(String iso2code) {
        return iso2code.matches("^[A-Z]{2}$");
    }

    private boolean validateSwiftCode(String swiftCode) {
        return swiftCode.matches("^[A-Z0-9]{11}$");
    }
}