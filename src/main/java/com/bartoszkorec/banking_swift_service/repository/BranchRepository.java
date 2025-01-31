package com.bartoszkorec.banking_swift_service.repository;

import com.bartoszkorec.banking_swift_service.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, String> {
}
