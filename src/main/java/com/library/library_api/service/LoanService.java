package com.library.library_api.service;


import com.library.library_api.dto.v1.LoanRequest;
import com.library.library_api.dto.v1.LoanResponse;
import com.library.library_api.exception.BookAlreadyLoanedOutException;
import com.library.library_api.exception.BookAlreadyReturnedException;
import com.library.library_api.exception.BookNotFoundException;
import com.library.library_api.exception.LoanNotFoundException;
import com.library.library_api.model.Book;
import com.library.library_api.model.Loan;
import com.library.library_api.repository.BookRepository;
import com.library.library_api.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    public LoanResponse createLoan(LoanRequest loanRequest) {
        Book book = bookRepository.findById(
                loanRequest.bookId())
                .orElseThrow(() -> new BookNotFoundException(loanRequest.bookId()));
        loanRepository.findByBookIdAndReturnDateIsNull(loanRequest.bookId())
                .ifPresent(loan -> {
                    throw new BookAlreadyLoanedOutException(loanRequest.bookId());
                });
        Loan loan = new Loan(book);
        Loan savedLoan = loanRepository.save(loan);
        return toResponse(savedLoan);

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

    public LoanResponse returnLoanedBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));
        if(loan.getReturnDate() != null) {
            throw new BookAlreadyReturnedException(loanId);
        }
        loan.setReturnDate(LocalDate.now());
        Loan savedLoan = loanRepository.save(loan);
        return toResponse(savedLoan);
    }
}
