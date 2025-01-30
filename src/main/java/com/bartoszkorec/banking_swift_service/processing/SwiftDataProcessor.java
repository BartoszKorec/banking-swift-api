package com.bartoszkorec.banking_swift_service.processing;

import com.bartoszkorec.banking_swift_service.dto.parser.*;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Country;
import com.bartoszkorec.banking_swift_service.entity.Headquarter;
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
    private final Map<String, LocationDTO> locations = new HashMap<>();
    private final Map<String, CountryDTO> countries = new HashMap<>();

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

        if (swiftCode.endsWith("XXX")) {
            processHeadquarter(iso2code, swiftCode, name, address, countryName, lineNumber);
        } else {
            processBranch(iso2code, swiftCode, name, address, countryName, lineNumber);
        }
    }
//
//    private void processHeadquarter(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
//        if (headquarters.containsKey(swiftCode)) {
////            log.warn("Line {}: Duplicate headquarter found with Swift code: {}", lineNumber, swiftCode);
//            return;
//        }
//        String swiftPrefix = swiftCode.substring(0, 8);
//        var correspondingBranches = branches.entrySet().stream()
//                .filter(e -> e.getKey().contains(swiftPrefix))
//                .map(Map.Entry::getValue)
//                .collect(Collectors.toSet());
//        HeadquarterDTO headquarter = new HeadquarterDTO(address, name, iso2code, countryName, swiftCode);
//        headquarter.getBranches().addAll(correspondingBranches);
//        headquarters.put(swiftCode, headquarter);
//    }
//
//    private void processBranch(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {
//
//        if (branches.containsKey(swiftCode)) {

    /// /            log.warn("Line {}: Duplicate branch found with Swift code: {}", lineNumber, swiftCode);
//            return;
//        }
//        branches.put(swiftCode, new BranchDTO(address, name, iso2code, countryName, swiftCode));
//    }
//
//    private List<String> validateRequiredFields(String... fields) {
//        List<String> missingFields = new ArrayList<>();
//        String[] fieldNames = {"COUNTRY ISO2 CODE", "SWIFT CODE", "NAME", "ADDRESS", "COUNTRY NAME"};
//
//        for (int i = 0; i < fields.length; i++) {
//            if (fields[i].isBlank()) {
//                missingFields.add(fieldNames[i]);
//            }
//        }
//        return missingFields;
//    }
//}
    private void processHeadquarter(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {

        if (headquarters.containsKey(swiftCode)) {
//            log.warn("Line {}: Duplicate headquarter found with Swift code: {}", lineNumber, swiftCode);
            return;
        }
        LocationDTO location = findOrCreateLocation(iso2code, address, countryName);
        Headquarter headquarter = new Headquarter();
        headquarter.setSwiftCode(swiftCode);
        headquarter.setName(name);
        headquarter.setFkLocation(location);
        headquarters.put(swiftCode, headquarter);
    }

    private void processBranch(String iso2code, String swiftCode, String name, String address, String countryName, String lineNumber) {

        String swiftCodePrefix = swiftCode.substring(0, 8);
        if (branches.containsKey(swiftCode)) {
            System.err.println("Line " + lineNumber + ": Duplicate branch found with Swift code: " + swiftCode);
            return;
        }
        String headquarterSwiftCode = swiftCodePrefix + "XXX";
        Headquarter headquarter = headquarters.get(headquarterSwiftCode);
        if (headquarter == null) {
            System.err.println("Line " + lineNumber + ": No matching headquarter found for branch with Swift code: " + swiftCode);
            return;
        }

        Location location = findOrCreateLocation(iso2code, address, countryName);
        Branch branch = new Branch();
        branch.setSwiftCode(swiftCode);
        branch.setName(name);
        branch.setFkLocation(location);
        branch.setFkHeadquarter(headquarter);
        branches.put(swiftCode, branch);
    }

    private LocationDTO findOrCreateLocation(String iso2code, String address, String countryName) {

        for (LocationDTO l : locations) {
            if (l.getAddress().equals(address)) {
                return l;
            }
        }
        LocationDTO newLocation = new LocationDTO(address, findOrCreateCountry(iso2code, countryName));
        locations.add(newLocation);
        return newLocation;
    }

    private CountryDTO findOrCreateCountry(String iso2code, String countryName) {
        countries.putIfAbsent(iso2code, new CountryDTO(iso2code, countryName));
        return countries.get(iso2code);
    }
}