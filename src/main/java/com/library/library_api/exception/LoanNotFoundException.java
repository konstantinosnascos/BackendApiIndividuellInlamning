package com.library.library_api.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) {
        super("Loan with id " + id + " not found");
    }
}
