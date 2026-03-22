package com.library.library_api.exception;

public class InvalidReturnDateException extends RuntimeException {
    public InvalidReturnDateException() {
        super("Return date cannot be before loan date");
    }
}