package com.library.library_api.controller.v1;

import com.library.library_api.dto.v1.LoanRequest;
import com.library.library_api.dto.v1.LoanResponse;
import com.library.library_api.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(
            @Valid
            @RequestBody LoanRequest loanRequest) {
        LoanResponse loanResponse = loanService.createLoan(loanRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);
    }

}
