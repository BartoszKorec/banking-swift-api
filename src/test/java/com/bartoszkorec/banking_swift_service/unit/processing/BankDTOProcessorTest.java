package com.bartoszkorec.banking_swift_service.unit.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.processor.BankDTOProcessor;
import com.bartoszkorec.banking_swift_service.validation.CountryCodeValidator;
import com.bartoszkorec.banking_swift_service.validation.CountryNameValidator;
import com.bartoszkorec.banking_swift_service.validation.SwiftCodeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BankDTOProcessorTest {

    private final BankDTOProcessor processor = new BankDTOProcessor(new CountryCodeValidator(), new CountryNameValidator(), new SwiftCodeValidator());
    private BankDTO bankDTO;

    @BeforeEach
    void setUp() {
        bankDTO = BankDTO.builder()
                .address(" some address ")
                .bankName(" some bank ")
                .countryISO2(" lv ")
                .countryName(" latvia ")
                .build();
    }

    @Test
    void shouldMarkAsHeadquartersAndCreateBranches() {
        // Given
        bankDTO.setSwiftCode("12345678XXX");
        // When
        BankDTO result = processor.processBankDTO(bankDTO);

        // Then
        assertThat(result.isHeadquarters(), is(equalTo(true)));
        assertThat(result.getBranches(), is(emptyCollectionOf(BankDTO.class)));
    }

    @Test
    void shouldNotMarkAsHeadquartersAndNullifyBranches() {
        // Given
        bankDTO.setSwiftCode("12345678AAA");

        // When
        BankDTO result = processor.processBankDTO(bankDTO);

        // Then
        assertThat(result.isHeadquarters(), is(equalTo(false)));
        assertThat(result.getBranches(), is(nullValue()));
    }
}