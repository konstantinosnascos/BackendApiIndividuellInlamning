package com.library.library_api.exception;

public class HttpRequestMethodNotSupportedException extends RuntimeException {
    public HttpRequestMethodNotSupportedException(Long id) {
        super("method not supported or wrongly entered");
    }
}
