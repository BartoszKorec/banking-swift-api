package com.bartoszkorec.banking_swift_service.repository;

import com.bartoszkorec.banking_swift_service.entity.Headquarter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeadquarterRepository extends JpaRepository<Headquarter, String> {
}
