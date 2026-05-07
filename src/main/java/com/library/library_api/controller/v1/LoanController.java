package com.library.library_api.controller.v1;

import com.library.library_api.dto.v1.LoanRequest;
import com.library.library_api.dto.v1.LoanResponse;
import com.library.library_api.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@Tag(name = "Loans", description = "Loan management endpoints")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    //kom ihåg, dubbelkolla response codes på loan(book not found, bookk loaned out etc)
    @Operation(summary = "Create a new loan", description = "Create a new loan and return that loan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "409", description = "Book is already on loan")
    })
    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(
            @Valid
            @RequestBody LoanRequest loanRequest) {
        LoanResponse loanResponse = loanService.createLoan(loanRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);
    }

    @Operation(summary = "Get all loans", description = "Return a list of all loans")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all loans")
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    //autogenererade @apiresponse 409,kolla upp 409(såg också nämnt i kursmaterial någonstans och inte satt upp felhantering för 409)
    @Operation(summary = "Return a loaned book", description = "Return a loaned book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loaned book returned successfully"),
        @ApiResponse(responseCode = "404", description = "Loan not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Book has already been returned")
    })
    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnLoanedBook(@PathVariable Long id,
                                                         @RequestBody(required = false) LoanRequest loanRequest){
        LocalDate returnDate = loanRequest != null ? loanRequest.returnDate() : null;
        return ResponseEntity.ok(loanService.returnLoanedBook(id, returnDate));
    }

    @Operation(summary = "Get loan by ID", description = "Return a loan by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan found"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @Operation(summary = "Delete loan by ID", description = "Delete a loan by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Loan deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }


}
