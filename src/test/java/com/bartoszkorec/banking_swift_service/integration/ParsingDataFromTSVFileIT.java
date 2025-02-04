package com.bartoszkorec.banking_swift_service.integration;

import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import com.bartoszkorec.banking_swift_service.service.SwiftDataProcessorService;
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
public class ParsingDataFromTSVFileIT {

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

    @Value("classpath:correct-test-data.tsv")
    private Path correctDataPath;

    @Value("classpath:invalid-test-data.tsv")
    private Path invalidDataPath;

    private final SwiftDataProcessorService service;
    private final BranchRepository branchRepository;
    private final HeadquartersRepository headquartersRepository;

    @Autowired
    public ParsingDataFromTSVFileIT(SwiftDataProcessorService service, BranchRepository branchRepository, HeadquartersRepository headquartersRepository) {
        this.service = service;
        this.branchRepository = branchRepository;
        this.headquartersRepository = headquartersRepository;
    }

    @BeforeEach
    void setUp() {
        branchRepository.deleteAll();
        headquartersRepository.deleteAll();
    }

//    @Test
//    void shouldStoreCorrectData() {
//
//        // When
//
//        // Then
//        assertThat(branchRepository.existsById("BIGBPLPWCUS"), is(equalTo(true)));
//        assertThat(headquartersRepository.existsById("BIGBPLPWXXX"), is(equalTo(true)));
//        assertThat(branchRepository.existsById("BIGBPLPWCUX"), is(equalTo(true)));
//    }
//
//    @Test
//    void shouldNotStoreInvalidData() {
//
//        // When
//
//        // Then
//        assertThat(branchRepository.existsById("AIZKLV22CLN"), is(equalTo(false)));
//        assertThat(headquartersRepository.existsById("AIZklv22CLN"), is(equalTo(false)));
//        assertThat(headquartersRepository.existsById("AIZKLV22CLN1"), is(equalTo(false)));
//        assertThat(headquartersRepository.existsById("AIZKLV2XXX"), is(equalTo(false)));
//        assertThat(headquartersRepository.existsById("BERLMCMCBDF"), is(equalTo(false)));
//    }
}
