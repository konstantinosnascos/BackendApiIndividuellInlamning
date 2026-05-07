package com.library.library_api.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LoanRequest (
        @Schema(description = "Book id", example = "1")
        @NotNull(message = "Book id is required") Long bookId,

        @Schema(description = "Loan date", example = "2023-01-01") LocalDate returnDate
) {}