package com.library.library_api.dto.v1;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LoanRequest (
        @NotNull(message = "Book id is required")
        Long bookId,
        LocalDate returnDate
) {}