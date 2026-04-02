package com.library.library_api.service;


import com.library.library_api.dto.v1.LoanRequest;
import com.library.library_api.dto.v1.LoanResponse;
import com.library.library_api.exception.*;
import com.library.library_api.model.Book;
import com.library.library_api.model.Loan;
import com.library.library_api.repository.BookRepository;
import com.library.library_api.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    @Value("${loan.create.delay-ms:0}")
    private long createLoanDelayMs;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public LoanResponse createLoan(LoanRequest loanRequest) {
        Book book = bookRepository.findById(
                loanRequest.bookId())
                .orElseThrow(() -> new BookNotFoundException(loanRequest.bookId()));
        loanRepository.findByBookIdAndReturnDateIsNull(loanRequest.bookId())
                .ifPresent(loan -> {
                    throw new BookAlreadyLoanedOutException(loanRequest.bookId());
                });

        applyArtificialDelayIfConfigured();
        try {
            Loan loan = new Loan(book);
            Loan savedLoan = loanRepository.save(loan);
            return toResponse(savedLoan);
        } catch (DataIntegrityViolationException e) {
            throw new BookAlreadyLoanedOutException(loanRequest.bookId());
        }
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getLoanDate(),
                loan.getReturnDate()
        );
    }

    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .filter(loan -> loan.getReturnDate() == null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LoanResponse returnLoanedBook(Long loanId, LocalDate returnDate) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));
        if(loan.getReturnDate() != null) {
            throw new BookAlreadyReturnedException(loanId);
        }

        LocalDate chosenReturnDate = returnDate != null ? returnDate : LocalDate.now();

        if(chosenReturnDate.isBefore(loan.getLoanDate())) {
            throw new InvalidReturnDateException();
        }
        loan.setReturnDate(chosenReturnDate);
        Loan savedLoan = loanRepository.save(loan);
        return toResponse(savedLoan);
    }

    private void applyArtificialDelayIfConfigured() {
        if (createLoanDelayMs > 0) {
            try {
                Thread.sleep(createLoanDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted", e);
            }
        }
    }
}
