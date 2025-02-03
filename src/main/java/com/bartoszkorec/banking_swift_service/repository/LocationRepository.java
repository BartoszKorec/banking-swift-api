package com.bartoszkorec.banking_swift_service.repository;

import com.bartoszkorec.banking_swift_service.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    /**
     *
     * @param addressLine The address of the location
     * @return An optional containing location matching the criteria
     */
    Optional<Location> findByAddressLine(String addressLine);
}
