package com.library.library_api.exception;

public class BookAlreadyLoanedOutException extends RuntimeException {
    public BookAlreadyLoanedOutException(Long bookId) {
        super("Book with id " + bookId + " is already on loan");
    }
}