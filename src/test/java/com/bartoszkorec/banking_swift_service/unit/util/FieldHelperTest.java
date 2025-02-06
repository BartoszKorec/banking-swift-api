package com.bartoszkorec.banking_swift_service.unit.util;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.util.FieldHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class FieldHelperTest {


    @ParameterizedTest(name = "when input is \"{0}\" and toUpperCase is {1} then result should be \"{2}\"")
    @CsvSource({
            "'  abc  ', true, 'ABC'",
            "'  AbC  ', false, 'AbC'",
            "'def', true, 'DEF'",
            "' DEF ', false, 'DEF'"
    })
    void shouldTrimAndConditionallyUppercaseValidInput(String input, boolean toUpperCase, String expected) {
        // Given
        String fieldName = "Test Field";

        // When
        String actual = FieldHelper.validateAndTrim(input, toUpperCase, fieldName);

        // Then
        assertThat("The trimmed (and uppercased) value should match the expected result",
                actual, is(equalTo(expected)));
    }

    @ParameterizedTest(name = "when input is \"{0}\" then an InvalidFieldsException is thrown")
    @NullAndEmptySource
    @DisplayName("should throw exception for null or blank input")
    void shouldThrowExceptionForInvalidInput(String input) {
        // Given
        String fieldName = "Test Field";

        // When
        InvalidFieldsException thrown = assertThrows(InvalidFieldsException.class, () ->
                FieldHelper.validateAndTrim(input, true, fieldName));

        // Then
        assertThat("Exception message should indicate that the field is blank or null",
                thrown.getMessage(), is(equalTo("Test Field is blank or null")));
    }
}
