package com.bartoszkorec.banking_swift_service.unit.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.validation.CountryNameValidator;
import com.bartoszkorec.banking_swift_service.validation.FieldValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CountryNameValidatorTest {

    private final FieldValidator validator = new CountryNameValidator();

    @ParameterizedTest(name = "Given input \"{0}\", then the validated country name is \"{2}\"")
    @CsvSource({
            "' USA ', 'USA'",
            "' france ', 'FRANCE'",
            "'canada', 'CANADA'",
            "'GERMANY', 'GERMANY'"
    })
    void shouldReturnValidCountryNameAfterTrimmingAndUppercasing(String input, String expected) {
        // When
        String result = validator.validate(input);
        // Then
        assertThat("The returned country name should be trimmed and in uppercase",
                result, is(equalTo(expected)));
    }


    @ParameterizedTest(name = "Given input \"{0}\", then an InvalidFieldsException is thrown")
    @CsvSource({
            // Contains a space.
            "'United States'",
            // Contains a digit.
            "'Brasil1'",
            // Contains a special character.
            "'Mexi$co'",
            // Contains punctuation.
            "'U.K.'",
            // Contains a hyphen.
            "'Pol-and'"
    })
    void shouldThrowExceptionForInvalidCountryName(String input) {
        // When
        InvalidFieldsException thrown = assertThrows(InvalidFieldsException.class, () -> validator.validate(input));
        // Then
        assertThat("The thrown exception should be an instance of InvalidFieldsException",
                thrown, is(instanceOf(InvalidFieldsException.class)));
    }
}
