package com.bartoszkorec.banking_swift_service.unit.validation;

import org.junit.jupiter.api.DisplayName;

//import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SwiftDataValidator: Invalid bank data validations")
public class SwiftDataValidatorTest {

//    private static Stream<Arguments> invalidBankData() {
//        return Stream.of(
//                Arguments.of(new BankDTO("  ", "abc", "LV", "ABC", false, "AIZKLV22CLN", null), "blank bank address"),
//                Arguments.of(new BankDTO("abc", "  ", "LV", "ABC", false, "AIZKLV22CLN", null), "blank bank name"),
//                Arguments.of(new BankDTO("abc", "abc", "  ", "ABC", false, "AIZKLV22CLN", null), "blank iso2 code"),
//                Arguments.of(new BankDTO("abc", "abc", "lv", "ABC", false, "AIZKLV22CLN", null), "lowercase iso2 code"),
//                Arguments.of(new BankDTO("abc", "abc", "L", "ABC", false, "AIZKLV22CLN", null), "one-letter iso2 code"),
//                Arguments.of(new BankDTO("abc", "abc", "11", "ABC", false, "AIZKLV22CLN", null), "two-digit iso2 code"),
//                Arguments.of(new BankDTO("abc", "abc", "L1", "ABC", false, "AIZKLV22CLN", null), "alphanumeric iso2 code"),
//                Arguments.of(new BankDTO("abc", "abc", "LV", "Abc", false, "AIZKLV22CLN", null), "lowercase country name"),
//                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC12", false, "AIZKLV22CLN", null), "country name containing digits"),
//                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", false, "AIZKLV22CL", null), "swift code with 10 characters"),
//                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", false, "AIZKLV22CLN1", null), "swift code with 12 characters"),
//                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", false, "AIzKLV22CLN", null), "lowercase swift code"),
//                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", false, "AIZKLV22XXX", null), "branch code ending with XXX"),
//                Arguments.of(new BankDTO("abc", "abc", "LV", "ABC", true, "AIZKLV22CLN", null), "headquarters not ending with XXX")
//        );
//    }
//
//    private static Stream<Arguments> correctBankData() {
//        return Stream.of(
//                Arguments.of(new BankDTO("VALNU STREET 1  RIGA, RIGA, LV-1050", "NASDAQ CSD SE", "LV", "LATVIA", false, "LCDELV22LXX", null), "correct branch"),
//                Arguments.of(new BankDTO("VALNU STREET 1  RIGA, RIGA, LV-1050", "NASDAQ CSD SE", "LV", "LATVIA", false, "LCDELV22LVX", null), "correct branch"),
//                Arguments.of(new BankDTO("VALNU STREET 1  RIGA, RIGA, LV-1050", "NASDAQ CSD SE", "LV", "LATVIA", true, "LCDELV22XXX", null), "correct headquarters")
//        );
//    }
//
//    @ParameterizedTest(name = "Given a bank DTO with {1}, when validating fields, then InvalidFieldsException should be thrown")
//    @MethodSource("invalidBankData")
//    void shouldThrowExceptionForInvalidBankData(BankDTO bankDTO, String invalidScenario) {
//        assertThrows(InvalidFieldsException.class, () -> validateFields(
//                bankDTO.getCountryISO2(),
//                bankDTO.getSwiftCode(),
//                bankDTO.getBankName(),
//                bankDTO.getAddress(),
//                bankDTO.getCountryName(),
//                bankDTO.isHeadquarters()
//        ), "Expected InvalidFieldsException for " + invalidScenario);
//    }
//
//    @ParameterizedTest(name = "Given a bank DTO with {1}, when validating fields, then no exception should be thrown")
//    @MethodSource("correctBankData")
//    void shouldNotThrowExceptionForCorrectBankData(BankDTO bankDTO, String correctScenario) {
//        validateFields(
//                bankDTO.getCountryISO2(),
//                bankDTO.getSwiftCode(),
//                bankDTO.getBankName(),
//                bankDTO.getAddress(),
//                bankDTO.getCountryName(),
//                bankDTO.isHeadquarters()
//        );
//    }
}