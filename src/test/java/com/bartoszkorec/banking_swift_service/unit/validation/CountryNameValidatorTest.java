package com.bartoszkorec.banking_swift_service.unit.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.util.FieldHelper;
import com.bartoszkorec.banking_swift_service.validation.CountryNameValidator;
import com.bartoszkorec.banking_swift_service.validation.FieldValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class CountryNameValidatorTest {

    private final FieldValidator validator = new CountryNameValidator();

    @ParameterizedTest
    @ValueSource(strings = {"USA", "FRANCE", "CANADA", "GERMANY"})
    void shouldReturnValidCountryName(String input) {
        try (MockedStatic<FieldHelper> utilities = mockStatic(FieldHelper.class)) {
            // Given
            utilities.when(() -> FieldHelper.validateAndTrim(any(), anyBoolean(), any(), anyInt())).thenReturn(input);
            utilities.when(() -> FieldHelper.validateAndTrim(any(), anyBoolean(), any())).thenReturn(input);
            // When
            String result = validator.validate(input);
            // Then
            assertThat(result, is(equalTo(input)));
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "United States",
            "Brasil1",
            "Mexi$co",
            "U.K.",
            "Pol-and"
    })
    void shouldThrowExceptionForInvalidCountryName(String input) {
        try (MockedStatic<FieldHelper> utilities = mockStatic(FieldHelper.class)) {
            // Given
            utilities.when(() -> FieldHelper.validateAndTrim(any(), anyBoolean(), any(), anyInt())).thenReturn(input);
            utilities.when(() -> FieldHelper.validateAndTrim(any(), anyBoolean(), any())).thenReturn(input);
            // When
            InvalidFieldsException thrown = assertThrows(InvalidFieldsException.class, () -> validator.validate(input));
            // Then
            assertThat("The thrown exception should be an instance of InvalidFieldsException",
                    thrown, is(instanceOf(InvalidFieldsException.class)));
        }
    }
}
