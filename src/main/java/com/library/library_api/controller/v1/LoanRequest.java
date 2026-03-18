package com.library.library_api.controller.v1;

import jakarta.validation.constraints.NotNull;

public record LoanRequest (
        @NotNull(message = "Book id is required")
        Long bookId
) {}