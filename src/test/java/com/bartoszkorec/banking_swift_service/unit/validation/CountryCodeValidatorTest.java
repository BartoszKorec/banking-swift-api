package com.bartoszkorec.banking_swift_service.unit.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.validation.CountryCodeValidator;
import com.bartoszkorec.banking_swift_service.validation.FieldValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CountryCodeValidatorTest {

    private final FieldValidator validator = new CountryCodeValidator();

    @ParameterizedTest(name = "Given input \"{0}\", then the validated country code is \"{2}\"")
    @CsvSource({
            "'  us  ', 'US'",
            "'Gb', 'GB'",
            "'CA', 'CA'",
            "'  fr  ', 'FR'"
    })
    void shouldReturnValidCountryCodeAfterTrimmingAndUppercasing(String input, String expected) {
        // When
        String result = validator.validate(input);
        // Then
        assertThat("The returned country code should be trimmed and in uppercase",
                result, is(equalTo(expected)));
    }

    @ParameterizedTest(name = "Given input \"{0}\", then an exception is thrown")
    @CsvSource({
            // Too long (more than two letters)
            "USA",
            // Too short (only one letter)
            "U",
            // Contains numeric characters
            "1A",
            "A1",
            // Contains special characters
            "D#"
    })
    void shouldThrowExceptionForInvalidCountryCode(String input) {
        // When
        InvalidFieldsException thrown = assertThrows(InvalidFieldsException.class, () -> validator.validate(input));
        // Then
        assertThat("The exception message should indicate that the country code does not meet requirements",
                thrown, is(instanceOf(InvalidFieldsException.class)));
    }
}

