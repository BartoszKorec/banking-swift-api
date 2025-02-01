package com.bartoszkorec.banking_swift_service.integration;

import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import com.bartoszkorec.banking_swift_service.service.SwiftFileProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class SwiftDataIT {

    private final SwiftFileProcessorService service;
    private final BranchRepository branchRepository;
    private final HeadquartersRepository headquartersRepository;

    @Autowired
    public SwiftDataIT(SwiftFileProcessorService service, BranchRepository branchRepository, HeadquartersRepository headquartersRepository) {
        this.service = service;
        this.branchRepository = branchRepository;
        this.headquartersRepository = headquartersRepository;
    }

    @Test
    void correctDataShouldBeProcessedAndRetrieved() {
        // Given
        // When
        service.processSwiftFile();

        // Then
        assertThat(branchRepository.existsById("BIGBPLPWCUS"), is(equalTo(true)));
        assertThat(headquartersRepository.existsById("BIGBPLPWXXX"), is(equalTo(true)));
    }
}
