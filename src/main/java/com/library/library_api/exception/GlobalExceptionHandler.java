package com.library.library_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(
            BookNotFoundException ex, HttpServletRequest request) {

        logger.warn("Book not found: {}, path={}", ex.getMessage(), request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthorNotFound(
            AuthorNotFoundException ex, HttpServletRequest request) {

        logger.warn("Author not found: {}, path={}", ex.getMessage(), request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                        404, "Not Found",
                        ex.getMessage(),
                        request.getRequestURI());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.warn("Validation failed: {}, path={}", message, request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                400, "Bad Request", message, request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler (BookAlreadyLoanedOutException.class)
    public ResponseEntity<ErrorResponse> handleBookAlreadyLoanedOut(
            BookAlreadyLoanedOutException ex, HttpServletRequest request) {

        logger.warn("Loan rejected: {}, path={}", ex.getMessage(), request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                400, "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            @ExceptionHandler (BookAlreadyReturnedException.class)
    public ResponseEntity<ErrorResponse> handleBookAlreadyReturned(
            BookAlreadyReturnedException ex, HttpServletRequest request) {

                logger.warn("Return rejected: {}, path={}", ex.getMessage(), request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                 400, "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            @ExceptionHandler (LoanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoanNotFound(
            LoanNotFoundException ex, HttpServletRequest request) {

                logger.warn("Loan not found: {}, path={}", ex.getMessage(), request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                  404, "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            @ExceptionHandler (InvalidReturnDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReturnDate(
            InvalidReturnDateException ex, HttpServletRequest request) {

                logger.warn("Invalid return date: {}, path={}", ex.getMessage(), request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                   400, "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        logger.error("Unexpected error at path={}", request.getRequestURI(), ex); // Logga som ERROR med hela stacktracen

        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred", // Generellt meddelande till klienten
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
