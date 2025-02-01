package com.bartoszkorec.banking_swift_service.integration;

import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import com.bartoszkorec.banking_swift_service.service.SwiftFileProcessorService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


@SpringBootTest(properties = {"spring.main.banner-mode=off", "logging.level.root=warn"})
public class SwiftDataIT {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Value("classpath:test-swift-data.tsv")
    private Path testDataPath;
    private final SwiftFileProcessorService service;
    private final BranchRepository branchRepository;
    private final HeadquartersRepository headquartersRepository;

    @Autowired
    public SwiftDataIT(SwiftFileProcessorService service, BranchRepository branchRepository, HeadquartersRepository headquartersRepository) {
        this.service = service;
        this.branchRepository = branchRepository;
        this.headquartersRepository = headquartersRepository;
    }

    @BeforeEach
    void setUp() {
        branchRepository.deleteAll();
        headquartersRepository.deleteAll();
    }

    @Test
    void correctDataShouldBeProcessedAndRetrieved() {
        // Given
        // When
        service.processSwiftFile(testDataPath);

        // Then
        assertThat(branchRepository.existsById("BIGBPLPWCUS"), is(equalTo(true)));
        assertThat(headquartersRepository.existsById("BIGBPLPWXXX"), is(equalTo(true)));
    }
}
