package com.bartoszkorec.banking_swift_service.processing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
public class SwiftDataProcessorTest {

    private SwiftDataProcessor processor;
    private Stream<String> correctData;
    private Stream<String> badData;

    @BeforeEach
    void setUp() {
        processor = new SwiftDataProcessor();

        correctData = Stream.of("MC\tBERLMCMCBDF\tBIC11\tEDMOND DE ROTHSCHILD-MONACO\t  MONACO, MONACO, 98000\tMONACO\tMONACO\tEurope/Monaco\n", // correct branch, corresponding hq is below
                "MC\tBERLMCMCXXX\tBIC11\tEDMOND DE ROTHSCHILD-MONACO\tLES TERRASSES, CARLO 2 AVENUE DE MONTE MONACO, MONACO, 98000\tMONACO\tMONACO\tEurope/Monaco\n", // correct hq
                "MC\tBERLMCMCBDF\tBIC11\tEDMOND DE ROTHSCHILD-MONACO\t  MONACO, MONACO, 98000\tMONACO\tMONACO\tEurope/Monaco\n", // duplicated branch
                "MC\tBERLMCMCXXX\tBIC11\tEDMOND DE ROTHSCHILD-MONACO\tLES TERRASSES, CARLO 2 AVENUE DE MONTE MONACO, MONACO, 98000\tMONACO\tMONACO\tEurope/Monaco\n", // duplicated hq
                "PL\tALBPPLPWCUS\tBIC11\tALIOR BANK SPOLKA AKCYJNA\tLOPUSZANSKA BUSINESS PARK LOPUSZANSKA 38 D WARSZAWA, MAZOWIECKIE, 02-232\tWARSZAWA\tPOLAND\tEurope/Warsaw\n" // correct branch, but not having corresponding hq
        );

        badData = Stream.of("   \tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23  RIGA, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga\n", // blank iso2Code
                "LV\t   \tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23  RIGA, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga\n", // blank swiftCode,
                "LV\tAIZKLV22CLN\tBIC11\t   \tELIZABETES STREET 23  RIGA, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga\n", // blank bankName
                "LV\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\t   \tRIGA\tLATVIA\tEurope/Riga\n", // blank address
                "LV\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23  RIGA, RIGA, LV-1010\tRIGA\t  \tEurope/Riga\n", // blank countryName
                "11\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23  RIGA, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga\n", // iso2Code having 2 digits instead of 2 chars
                "L\tAIZKLV22CLN\tBIC11\tABLV BANK, AS IN LIQUIDATION\tELIZABETES STREET 23  RIGA, RIGA, LV-1010\tRIGA\tLATVIA\tEurope/Riga\n", // iso2Code having 1 char
                "LV\tAIZKLV2XXX\tBIC11\tABLV BANK, AS IN LIQUIDATION\tMIHAILA TALA STREET 1  RIGA, RIGA, LV-1045\tRIGA\tLATVIA\tEurope/Riga\n" // 10 chars swiftCode
        );
    }

//    @Test
//    void correctDataShouldBeAccessible() {
//        // Given
//        // When
//        processor.processLines(correctData);
//
//        // Then
//        assertThat(processor.getBranches(), hasKey("BERLMCMCBDF"));
//        assertThat(processor.getHeadquarters(), hasKey("BERLMCMCXXX"));
//    }
//
//    @Test
//    void invalidDataShouldNotBeAccessible() {
//        // Given
//        // When
//        processor.processLines(correctData);
//        processor.processLines(badData);
//
//        // Then
//        assertThat(processor.getBranches(), not(hasKey("ALBPPLPWCUS")));
//
//        assertThat(processor.getBranches(), not(hasKey("")));
//        assertThat(processor.getHeadquarters(), not(hasKey("")));
//
//        assertThat(processor.getBranches(), not(hasKey((String) null)));
//        assertThat(processor.getHeadquarters(), not(hasKey((String) null)));
//
//        assertThat(processor.getBranches(), not(hasKey("AIZKLV22CLN")));
//        assertThat(processor.getHeadquarters(), not(hasKey("AIZKLV2XXX")));
//    }
}
