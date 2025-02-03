package com.bartoszkorec.banking_swift_service.repository;

import com.bartoszkorec.banking_swift_service.entity.Headquarters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeadquartersRepository extends JpaRepository<Headquarters, String> {
}
