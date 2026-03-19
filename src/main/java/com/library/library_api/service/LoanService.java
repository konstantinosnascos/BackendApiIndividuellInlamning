package com.library.library_api.service;


import com.library.library_api.dto.v1.LoanRequest;
import com.library.library_api.dto.v1.LoanResponse;
import com.library.library_api.exception.BookAlreadyLoanedOutException;
import com.library.library_api.model.Book;
import com.library.library_api.model.Loan;
import com.library.library_api.repository.BookRepository;
import com.library.library_api.repository.LoanRepository;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new BookAlreadyLoanedOutException(loanRequest.bookId()));
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
}
