package com.library.library_api;

public class LoanIntegrationTest {

    // POST /api/v1/loans -> 201 create loan
    // POST /api/v1/loans -> 400 missing bookId
    // POST /api/v1/loans -> 404 book does not exist
    // POST /api/v1/loans -> 400 book already on loan

    // GET /api/v1/loans -> 200 list active loans
    // GET /api/v1/loans -> 200 empty list when no active loans exist

    // PATCH /api/v1/loans/{id}/return -> 200 return active loan
    // PATCH /api/v1/loans/{id}/return -> 404 missing loan
    // PATCH /api/v1/loans/{id}/return -> 400 already returned
    // PATCH /api/v1/loans/{id}/return -> 400 returnDate before loanDate


}
