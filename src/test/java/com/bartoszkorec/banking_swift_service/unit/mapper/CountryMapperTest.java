package com.bartoszkorec.banking_swift_service.unit.mapper;

import com.bartoszkorec.banking_swift_service.dto.BankDTO;
import com.bartoszkorec.banking_swift_service.dto.CountryDTO;
import com.bartoszkorec.banking_swift_service.entity.Branch;
import com.bartoszkorec.banking_swift_service.entity.Country;
import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import com.bartoszkorec.banking_swift_service.entity.Location;
import com.bartoszkorec.banking_swift_service.mapper.CountryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

class CountryMapperTest {

    private final CountryMapper countryMapper = Mappers.getMapper(CountryMapper.class);
    private Country country;
    private Headquarters hq;
    private Branch branch;
    private Location location;
    private CountryDTO countryDTO;


    @BeforeEach
    void setUp() {
        country = new Country();
        hq = new Headquarters();
        branch = new Branch();
        location = new Location();
        countryDTO = new CountryDTO();
    }

    @Test
    void mappedCountryDTOShouldContainBanksSwiftCodes() {

        // Given
        country.setCountryName("PL");
        country.setCountryName("POLAND");

        location.setCountry(country);
        location.setAddressLine("location test");

        String hqSwiftCode = "TEST1234XXX";
        hq.setSwiftCode(hqSwiftCode);
        hq.setName("hq test");

        String branchSwiftCode = "TEST1234123";
        branch.setSwiftCode(branchSwiftCode);
        branch.setName("branch test");
        branch.setHeadquarters(hq);
        branch.setLocation(location);

        hq.setBranches(Set.of(branch));
        location.setBranches(Set.of(branch));
        location.setHeadquarters(Set.of(hq));

        country.setLocations(Set.of(location));

        // When
        countryDTO = countryMapper.toDTO(country);

        // Then
        assertThat(countryDTO.getSwiftCodes().stream()
                .map(BankDTO::getSwiftCode)
                .collect(Collectors.toSet()), hasItems(hqSwiftCode, branchSwiftCode));
    }
}
