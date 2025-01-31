package com.bartoszkorec.banking_swift_service.repository;

import com.bartoszkorec.banking_swift_service.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
