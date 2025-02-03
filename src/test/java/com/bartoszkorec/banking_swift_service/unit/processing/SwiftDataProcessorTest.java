package com.bartoszkorec.banking_swift_service.unit.processing;

import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessor;
import com.bartoszkorec.banking_swift_service.processing.SwiftDataProcessorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SwiftDataProcessorImpl.class})
public class SwiftDataProcessorTest {


    @Autowired
    private SwiftDataProcessor processor;

    private Stream<String> correctData;
    private Stream<String> badData;

    @BeforeEach
    void setUp() {
        correctData = Stream.of(
                // Valid branch with corresponding HQ
                "MC\tBERLMCMCBDF\tBIC11\tEDMOND DE ROTHSCHILD-MONACO\tMONACO, MONACO, 98000\tMONACO\tMONACO\tEurope/Monaco",
                "MC\tBERLMCMCXXX\tBIC11\tEDMOND DE ROTHSCHILD-MONACO\tLES TERRASSES, MONACO, MONACO, 98000\tMONACO\tMONACO\tEurope/Monaco"
        );

        badData = Stream.of(
                // Invalid due to blank fields
                "   \tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga",
                "LV\t   \tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga",
                "LV\tAIZKLV22CLN\tBIC11\t   \tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga",
                "LV\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\t   \tRIGA\tLATVIA\tEurope/Riga",
                "LV\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\t  \tEurope/Riga",

                // Invalid ISO2 code cases
                "11\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga",
                "L\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga",
                "lv\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga",

                // Invalid country names
                "LV\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\tLatvia\tEurope/Riga",
                "LV\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23, RIGA, LV-1010\tRIGA\tLATVIA123\tEurope/Riga",

                // Invalid SWIFT codes
                "LV\tAIZKLV2XXX\tBIC11\tABLV BANK, AS IN LIQUIDATION\tMIHAILA TALA STREET 1, RIGA, LV-1045\tRIGA\tLATVIA\tEurope/Riga", // 10 chars
                "LV\tAIZKLV22CLN1\tBIC11\tABLV BANK, AS IN LIQUIDATION\tMIHAILA TALA STREET 1, RIGA, LV-1045\tRIGA\tLATVIA\tEurope/Riga", // 12 chars
                "LV\tAIZklv22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tMIHAILA TALA STREET 1, RIGA, LV-1045\tRIGA\tLATVIA\tEurope/Riga" // Lowercase SWIFT
        );
    }

    @Test
    void shouldStoreCorrectBankData() {
        // When
        processor.processLines(correctData);

        // Then
        assertThat(processor.getBanks(), hasKey("BERLMCMCBDF"));
        assertThat(processor.getBanks(), hasKey("BERLMCMCXXX"));
    }

    @Test
    void shouldNotStoreInvalidBankData() {
        // When
        processor.processLines(badData);

        // Then
        assertThat(processor.getBanks(), not(hasKey("")));
        assertThat(processor.getBanks(), not(hasKey("   ")));
        assertThat(processor.getBanks(), not(hasKey("AIZKLV22CLN")));
        assertThat(processor.getBanks(), not(hasKey("AIZKLV2XXX")));
        assertThat(processor.getBanks(), not(hasKey("AIZKLV22CLN1")));
        assertThat(processor.getBanks(), not(hasKey("AIZklv22CLN")));
    }
}