package com.bartoszkorec.banking_swift_service.unit.validation;

import com.bartoszkorec.banking_swift_service.exception.InvalidFieldsException;
import com.bartoszkorec.banking_swift_service.util.FieldHelper;
import com.bartoszkorec.banking_swift_service.validation.FieldValidator;
import com.bartoszkorec.banking_swift_service.validation.SwiftCodeValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class SwiftCodeValidatorTest {

    private final FieldValidator validator = new SwiftCodeValidator();

    @ParameterizedTest
    @ValueSource(strings = {"ABCDEF12345", "ABCDEF12345", "A1B2C3D4E5F"})
    void shouldReturnValidSwiftCode(String input) {
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
            "123456789X",
            "123456789XXX",
            "A1B2C3D4E5!",
            "ABC DEF1234"
    })
    void shouldThrowExceptionForInvalidSwiftCodeFormat(String input) {
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