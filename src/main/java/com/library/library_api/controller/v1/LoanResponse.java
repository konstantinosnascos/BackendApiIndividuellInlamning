package com.library.library_api.controller.v1;

import java.time.LocalDate;

public record LoanResponse (
    Long id,
    Long bookId,
    String bookTitle,
    LocalDate loanDate,
    LocalDate returnDate
) {}
