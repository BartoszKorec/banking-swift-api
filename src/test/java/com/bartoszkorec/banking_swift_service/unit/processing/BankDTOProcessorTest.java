package com.bartoszkorec.banking_swift_service.unit.processing;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.processor.BankDTOProcessor;
import com.bartoszkorec.banking_swift_service.util.FieldHelper;
import com.bartoszkorec.banking_swift_service.validation.CountryCodeValidator;
import com.bartoszkorec.banking_swift_service.validation.CountryNameValidator;
import com.bartoszkorec.banking_swift_service.validation.FieldValidator;
import com.bartoszkorec.banking_swift_service.validation.SwiftCodeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankDTOProcessorTest {

    private final FieldValidator countryCodeValidator = mock(CountryCodeValidator.class);
    private final FieldValidator countryNameValidator = mock(CountryNameValidator.class);
    private final FieldValidator swiftCodeValidator = mock(SwiftCodeValidator.class);
    private final BankDTOProcessor processor = new BankDTOProcessor(countryCodeValidator, countryNameValidator, swiftCodeValidator);
    private BankDTO bankDTO = new BankDTO();

    @Test
    void shouldMarkAsHeadquartersAndCreateBranches() {
        try (MockedStatic<FieldHelper> utilities = mockStatic(FieldHelper.class)) {
            // Given
            utilities.when(() -> FieldHelper.validateAndTrim(any(), anyBoolean(), any(), anyInt())).thenReturn("test");
            utilities.when(() -> FieldHelper.validateAndTrim(any(), anyBoolean(), any())).thenReturn("test");
            when(countryCodeValidator.validate(any(), anyInt())).thenReturn("test");
            when(countryNameValidator.validate(any(), anyInt())).thenReturn("test");
            when(swiftCodeValidator.validate(any(), anyInt())).thenReturn("12345678XXX");

            // When
            bankDTO = processor.processBankDTO(bankDTO);

            // Then
            assertThat(bankDTO.isHeadquarters(), is(equalTo(true)));
            assertThat(bankDTO.getBranches(), is(emptyCollectionOf(BankDTO.class)));
        }
    }

    @Test
    void shouldNotMarkAsHeadquartersAndNullifyBranches() {
        // Given
        try (MockedStatic<FieldHelper> utilities = mockStatic(FieldHelper.class)) {
            // Given
            utilities.when(() -> FieldHelper.validateAndTrim(any(), anyBoolean(), any(), anyInt())).thenReturn("test");
            utilities.when(() -> FieldHelper.validateAndTrim(any(), anyBoolean(), any())).thenReturn("test");
            when(countryCodeValidator.validate(any(), anyInt())).thenReturn("test");
            when(countryNameValidator.validate(any(), anyInt())).thenReturn("test");
            when(swiftCodeValidator.validate(any(), anyInt())).thenReturn("12345678AAX");

            // When
            bankDTO = processor.processBankDTO(bankDTO);

            // Then
            assertThat(bankDTO.isHeadquarters(), is(equalTo(false)));
            assertThat(bankDTO.getBranches(), is(nullValue()));
        }
    }
}