package com.bartoszkorec.banking_swift_service.unit.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.validation.FieldValidator;
import com.bartoszkorec.banking_swift_service.validation.SwiftCodeValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SwiftCodeValidatorTest {

    private final FieldValidator validator = new SwiftCodeValidator();

    @ParameterizedTest(name = "Given input \"{0}\" and line number {1}, then the validated SWIFT code should be \"{2}\"")
    @CsvSource({
            "'ABCDEF12345', 'ABCDEF12345'",
            "'   abcdef12345  ', 'ABCDEF12345'",
            "' a1b2c3d4e5f  ', 'A1B2C3D4E5F'"
    })
    void shouldReturnValidSwiftCodeAfterTrimmingAndUppercasing(String input, String expected) {
        // When
        String result = validator.validate(input);
        // Then
        assertThat("The returned SWIFT code should be trimmed and in uppercase",
                result, is(equalTo(expected)));
    }

    @ParameterizedTest(name = "Given input \"{0}\", then an exception with message \"{2}\" is thrown")
    @CsvSource({
            // Too short after trimming (10 characters instead of 11)
            "'abcdef1234', SWIFT code doesn't match requirements",
            // Too long after trimming (12 characters)
            "'ABCDEF123456', SWIFT code doesn't match requirements",
            // Contains an invalid character (exclamation mark)
            "'A1B2C3D4E5!', SWIFT code doesn't match requirements",
            // Contains an internal space (invalid character)
            "'ABC DEF12345', SWIFT code doesn't match requirements"
    })

    void shouldThrowExceptionForInvalidSwiftCodeFormat(String input, String expectedMessage) {
        // When
        InvalidFieldsException thrown = assertThrows(InvalidFieldsException.class, () -> {
            validator.validate(input);
        });
        // Then
        assertThat("The exception message should indicate that the SWIFT code doesn't match requirements",
                thrown.getMessage(), is(equalTo(expectedMessage)));
    }
}