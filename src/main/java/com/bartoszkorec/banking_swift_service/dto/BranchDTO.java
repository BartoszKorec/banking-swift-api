package com.bartoszkorec.banking_swift_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchDTO {

    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter = false;
    private String swiftCode;
}
