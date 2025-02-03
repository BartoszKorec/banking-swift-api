package com.bartoszkorec.banking_swift_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDTO {

    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarters;
    private String swiftCode;
}
