package com.example.demo.repository;

import com.example.demo.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, String>, JpaSpecificationExecutor<Fine> {
    Optional<Fine> findByLoanIdAndPaidFalse(String loanId);
}
