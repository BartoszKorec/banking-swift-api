package com.bartoszkorec.banking_swift_service.unit.mapper;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.mapper.BankMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BankMapperTest {

    private final BankMapper bankMapper = Mappers.getMapper(BankMapper.class);
    private Branch branch;
    private Headquarters headquarters;

    @BeforeEach
    void setUp() {
        branch = new Branch();
        headquarters = new Headquarters();
    }

    @Test
    void mappedBranchToDTOShouldNotBeHeadquarters() {
        // Given
        // When
        BankDTO bankDTO = bankMapper.toDTO(branch);

        // Then
        assertThat(bankDTO.isHeadquarters(), is(equalTo(false)));
    }

    @Test
    void mappedHeadquartersToDTOShouldBeHeadquarters() {
        // Given
        // When
        BankDTO bankDTO = bankMapper.toDTO(headquarters);

        // Then
        assertThat(bankDTO.isHeadquarters(), is(equalTo(true)));
    }

    @Test
    void mappedBranchShouldHaveProperlyMappedHeadquartersSwiftCode() {
        // Given
        BankDTO bankDTO = new BankDTO();
        bankDTO.setSwiftCode("12345678ABC");

        // When
        branch = (Branch) bankMapper.toEntity(bankDTO);

        // Then
        assertThat(branch.getHeadquarters().getSwiftCode(), is(equalTo("12345678XXX")));
    }
}
