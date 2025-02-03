package com.bartoszkorec.banking_swift_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({
        "address",
        "bankName",
        "countryISO2",
        "countryName",
        "isHeadquarters",
        "swiftCode",
        "branches"
})
public class BankDTO {

    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;

    @JsonProperty("isHeadquarters")
    private boolean isHeadquarters;

    private String swiftCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<BankDTO> branches;
}
