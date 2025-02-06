package com.bartoszkorec.banking_swift_service.integration;

import com.bartoszkorec.banking_swift_service.reader.ParseDTOsToDatabase;
import com.bartoszkorec.banking_swift_service.repository.BranchRepository;
import com.bartoszkorec.banking_swift_service.repository.HeadquartersRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TSVFileParsingToDatabaseAndRestIT {

    @LocalServerPort
    private Integer port;

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

    @Autowired
    HeadquartersRepository headquartersRepository;
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    ParseDTOsToDatabase parseDTOsToDatabase;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        headquartersRepository.deleteAll();
    }

    @Test
    void shouldProcessOnlyCorrectData() {
        // When
        parseDTOsToDatabase.addDataToDatabase();

        // Then
        assertThat(headquartersRepository.count(), is(equalTo(1L)));
        assertThat(branchRepository.count(), is(equalTo(1L)));
    }

    @Test
    void shouldGetSpecifiedBanks() {
        // When
        parseDTOsToDatabase.addDataToDatabase();

        // Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/swift-codes/CEDPBGSFXXX")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldGetSpecifiedCountry() {
        // When
        parseDTOsToDatabase.addDataToDatabase();

        // Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("v1/swift-codes/country/bg")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldDeleteSpecifiedBank() {
        // When
        parseDTOsToDatabase.addDataToDatabase();

        given().delete("v1/swift-codes/CEDPBGSFXXX")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Then
        assertThat(headquartersRepository.count(), is(equalTo(0L)));
    }

    @Test
    void shouldAddCorrectBankData() {
        // Given
        String bankJson = """
                {
                    "address": "AV. APOQUINDO 2827, FLOOR 13 LAS CONDES - SANTIAGO, PROVINCIA DE SANTIAGO, 7590956",
                    "bankName": "JPMORGAN CHASE BANK, N.A., SANTIAGO BRANCH",
                    "countryISO2": "CL",
                    "countryName": "CHILE",
                    "isHeadquarters": true,
                    "swiftCode": "CHASCLRMXXX",
                    "branches": [
                        {
                            "address": "AV. APOQUINDO 2827, FLOOR 13 LAS CONDES - SANTIAGO, PROVINCIA DE SANTIAGO, 7590956",
                            "bankName": "JPMORGAN CHASE BANK, N.A., SANTIAGO BRANCH",
                            "countryISO2": "CL",
                            "countryName": "CHILE",
                            "isHeadquarters": false,
                            "swiftCode": "CHASCLRMICM"
                        }
                    ]
                }
                """;

        // When
        given()
                .contentType(ContentType.JSON)
                .body(bankJson)
                .when()
                .post("/v1/swift-codes")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Then
        assertThat(headquartersRepository.count(), is(equalTo(1L)));
        assertThat(headquartersRepository.findById("CHASCLRMXXX").isPresent(), is(true));

        assertThat(branchRepository.count(), is(equalTo(1L)));
        assertThat(branchRepository.findById("CHASCLRMICM").isPresent(), is(true));
    }

    @Test
    void shouldReturnBadRequestForInvalidBankData() {
        // Given
        String invalidBankJson = """
                {
                    "address": "",
                    "bankName": "",
                    "countryISO2": "INVALID",
                    "countryName": "",
                    "isHeadquarters": true,
                    "swiftCode": "INVALIDSWIFT",
                    "branches": [
                        {
                            "address": "",
                            "bankName": "",
                            "countryISO2": "INVALID",
                            "countryName": "",
                            "isHeadquarters": false,
                            "swiftCode": "INVALIDSWIFTBRANCH"
                        }
                    ]
                }
                """;

        // When
        given()
                .contentType(ContentType.JSON)
                .body(invalidBankJson)
                .when()
                .post("/v1/swift-codes")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // Then
        assertThat(headquartersRepository.count(), is(equalTo(0L)));
        assertThat(branchRepository.count(), is(equalTo(0L)));
    }

    @Test
    void shouldReturnBadRequestForInvalidBranchData() {
        // Given
        String invalidBranchJson = """
                {
                    "address": "AV. APOQUINDO 2827, FLOOR 13 LAS CONDES - SANTIAGO, PROVINCIA DE SANTIAGO, 7590956",
                    "bankName": "JPMORGAN CHASE BANK, N.A., SANTIAGO BRANCH",
                    "countryISO2": "CL",
                    "countryName": "CHILE",
                    "isHeadquarters": true,
                    "swiftCode": "CHASCLRMXXX",
                    "branches": [
                        {
                            "address": "",
                            "bankName": "",
                            "countryISO2": "INVALID",
                            "countryName": "",
                            "isHeadquarters": false,
                            "swiftCode": "INVALIDSWIFTBRANCH"
                        }
                    ]
                }
                """;

        // When
        given()
                .contentType(ContentType.JSON)
                .body(invalidBranchJson)
                .when()
                .post("/v1/swift-codes")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // Then
        assertThat(headquartersRepository.count(), is(equalTo(0L)));
        assertThat(branchRepository.count(), is(equalTo(0L)));
    }

    @Test
    void shouldAddSpecifiedBranch() {
        // Given
        parseDTOsToDatabase.addDataToDatabase();
        String branchJson = """
                {
                            "address": "AV. APOQUINDO 2827, FLOOR 13 LAS CONDES - SANTIAGO, PROVINCIA DE SANTIAGO, 7590956",
                            "bankName": "JPMORGAN CHASE BANK, N.A., SANTIAGO BRANCH",
                            "countryISO2": "CL",
                            "countryName": "CHILE",
                            "isHeadquarters": false,
                            "swiftCode": "CEDPBGSFTST"
                        }
                """;

        // When
        given()
                .contentType(ContentType.JSON)
                .body(branchJson)
                .when()
                .post("/v1/swift-codes")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Then
        assertThat(branchRepository.count(), is(equalTo(2L)));
        assertThat(branchRepository.findById("CEDPBGSFTST").isPresent(), is(true));
    }

    @Test
    void  shouldReturnBadRequestWhenBranchHasNoCorrespondingHeadquarters() {
        // Given
        String branchJson = """
                {
                            "address": "AV. APOQUINDO 2827, FLOOR 13 LAS CONDES - SANTIAGO, PROVINCIA DE SANTIAGO, 7590956",
                            "bankName": "JPMORGAN CHASE BANK, N.A., SANTIAGO BRANCH",
                            "countryISO2": "CL",
                            "countryName": "CHILE",
                            "isHeadquarters": false,
                            "swiftCode": "CEDPBGSFTST"
                        }
                """;

        // When
        given()
                .contentType(ContentType.JSON)
                .body(branchJson)
                .when()
                .post("/v1/swift-codes")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

        // Then
        assertThat(branchRepository.count(), is(equalTo(0L)));
    }

    @Test
    void shouldReturnConflictWhenAddingDuplicateBank() {
        // Given
        String bankJson = """
            {
                "address": "AV. APOQUINDO 2827, FLOOR 13 LAS CONDES - SANTIAGO, PROVINCIA DE SANTIAGO, 7590956",
                "bankName": "JPMORGAN CHASE BANK, N.A., SANTIAGO BRANCH",
                "countryISO2": "CL",
                "countryName": "CHILE",
                "isHeadquarters": true,
                "swiftCode": "CHASCLRMXXX"
            }
            """;

        // When
        given()
                .contentType(ContentType.JSON)
                .body(bankJson)
                .when()
                .post("/v1/swift-codes")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .body(bankJson)
                .when()
                .post("/v1/swift-codes")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());

        // Then
        assertThat(headquartersRepository.count(), is(equalTo(1L)));
    }
}