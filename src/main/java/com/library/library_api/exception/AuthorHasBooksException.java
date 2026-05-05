package com.library.library_api.exception;

public class AuthorHasBooksException extends RuntimeException {
    public AuthorHasBooksException(Long id) {
        super("Author with id " + id + " cannot be deleted because they have books");
    }
}
