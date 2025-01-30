package com.bartoszkorec.banking_swift_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class HeadquarterDTO {

    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter = true;
    private String swiftCode;
    private Set<BranchDTO> branches;
}
