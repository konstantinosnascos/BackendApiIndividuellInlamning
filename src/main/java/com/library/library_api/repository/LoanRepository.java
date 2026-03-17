package com.library.library_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.library_api.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
