package com.library.library_api.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record LoanResponse (

    @Schema(description = "Loan ID", example = "1") Long id,

    @Schema(description = "Book ID", example = "1") Long bookId,

    @Schema(description = "Book title", example = "Clean Code") String bookTitle,

    @Schema(description = "Loan date", example = "2023-01-01") LocalDate loanDate,

    @Schema(description = "Return date", example = "2023-01-02") LocalDate returnDate
) {}
