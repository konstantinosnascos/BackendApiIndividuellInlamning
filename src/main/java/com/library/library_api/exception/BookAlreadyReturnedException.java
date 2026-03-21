package com.library.library_api.exception;

public class BookAlreadyReturnedException extends RuntimeException {
    public BookAlreadyReturnedException(Long loanId) {
        super("Loan with id " + loanId + " has already been returned");
    }
}