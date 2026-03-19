package com.library.library_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.library_api.model.Loan;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByBookIdAndReturnDateIsNull(Long bookId);
}
