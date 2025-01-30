package com.bartoszkorec.banking_swift_service.dto.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class HeadquarterDTO {

    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter = true;
    private String swiftCode;
    private Set<BranchDTO> branches = new HashSet<>();

    public HeadquarterDTO(String address, String bankName, String countryISO2, String countryName, String swiftCode) {
        this.address = address;
        this.bankName = bankName;
        this.countryISO2 = countryISO2;
        this.countryName = countryName;
        this.swiftCode = swiftCode;
    }

    // some building problems with getBranches
    public Set<BranchDTO> getBranches() {
        return branches;
    }
}
