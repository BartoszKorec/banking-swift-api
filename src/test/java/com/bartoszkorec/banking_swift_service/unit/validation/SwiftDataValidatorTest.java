package com.bartoszkorec.banking_swift_service.unit.validation;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.bartoszkorec.banking_swift_service.validation.SwiftDataValidator.validateFields;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@DisplayName("SwiftDataValidator: Invalid bank data validations")
public class SwiftDataValidatorTest {

    private static Stream<Arguments> invalidBankData() {
        return Stream.of(
                Arguments.of(new BankDTO("  ", "abc", "LV", "ABC", false, "AIZKLV22CLN", null), "blank bank address"),
                Arguments.of(new BankDTO("abc", "  ", "LV", "ABC", false, "AIZKLV22CLN", null), "blank bank name"),
                Arguments.of(new BankDTO("abc", "abc", "  ", "ABC", false, "AIZKLV22CLN", null), "blank iso2 code"),
                Arguments.of(new BankDTO("abc", "abc", "lv", "ABC", false, "AIZKLV22CLN", null), "lowercase iso2 code"),
                Arguments.of(new BankDTO("abc", "abc", "L", "ABC", false, "AIZKLV22CLN", null), "one-letter iso2 code"),
                Arguments.of(new BankDTO("abc", "abc", "11", "ABC", false, "AIZKLV22CLN", null), "two-digit iso2 code"),
                Arguments.of(new BankDTO("abc", "abc", "L1", "ABC", false, "AIZKLV22CLN", null), "alphanumeric iso2 code"),
                Arguments.of(new BankDTO("abc", "abc", "LV", "Abc", false, "AIZKLV22CLN", null), "lowercase country name"),
                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC12", false, "AIZKLV22CLN", null), "country name containing digits"),
                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", false, "AIZKLV22CL", null), "swift code with 10 characters"),
                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", false, "AIZKLV22CLN1", null), "swift code with 12 characters"),
                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", false, "AIzKLV22CLN", null), "lowercase swift code"),
                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", false, "AIZKLV22XXX", null), "branch code ending with XXX"),
                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", true, "AIZKLV22CLN", null), "headquarters not ending with XXX")
        );
    }

    private static Stream<Arguments> correctBankData() {
        return Stream.of(
                Arguments.of(new BankDTO("  MONACO, MONACO, 98000", "EDMOND DE ROTHSCHILD-MONACO", "MC", "MONACO", false, "BERLMCMCBDF", null), "correct branch"),
                Arguments.of(new BankDTO("LES TERRASSES, CARLO 2 AVENUE DE MONTE MONACO", "EDMOND DE ROTHSCHILD-MONACO", "MC", "MONACO", true, "BERLMCMCXXX", null), "correct headquarters")
        );
    }

    @ParameterizedTest(name = "Given a bank DTO with {1}, when validating fields, then result should be false")
    @MethodSource("invalidBankData")
    void shouldReturnFalseForInvalidBankData(BankDTO bankDTO, String invalidScenario) {
        boolean isValid = validateFields(
                bankDTO.getCountryISO2(),
                bankDTO.getSwiftCode(),
                bankDTO.getBankName(),
                bankDTO.getAddress(),
                bankDTO.getCountryName(),
                bankDTO.isHeadquarters()
        );
        assertThat("Expected false for " + invalidScenario, isValid, is(equalTo(false)));
    }

    @ParameterizedTest(name = "Given a bank DTO with {1}, when validating fields, then result should be true")
    @MethodSource("correctBankData")
    void shouldReturnTrueForCorrectBankData(BankDTO bankDTO, String correctScenario) {
        boolean isValid = validateFields(
                bankDTO.getCountryISO2(),
                bankDTO.getSwiftCode(),
                bankDTO.getBankName(),
                bankDTO.getAddress(),
                bankDTO.getCountryName(),
                bankDTO.isHeadquarters()
        );
        assertThat("Expected true for " + correctScenario, isValid, is(equalTo(true)));
    }
}