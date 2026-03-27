package com.library.library_api;


import com.library.library_api.dto.v1.BookRequest;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.dto.v1.LoanRequest;
import com.library.library_api.dto.v1.LoanResponse;
import com.library.library_api.repository.AuthorRepository;
import com.library.library_api.repository.BookRepository;
import com.library.library_api.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoanIntegrationTest {

    // POST /api/v1/loans -> 201 create loan -klar
    // POST /api/v1/loans -> 400 missing bookId -klar
    // POST /api/v1/loans -> 404 book does not exist
    // POST /api/v1/loans -> 400 book already on loan

    // GET /api/v1/loans -> 200 list active loans
    // GET /api/v1/loans -> 200 empty list when no active loans exist

    // PATCH /api/v1/loans/{id}/return -> 200 return active loan
    // PATCH /api/v1/loans/{id}/return -> 404 missing loan
    // PATCH /api/v1/loans/{id}/return -> 400 already returned
    // PATCH /api/v1/loans/{id}/return -> 400 returnDate before loanDate


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    void createLoan_shouldReturn201AndSavedLoan() {
        BookRequest bookRequest = new BookRequest(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                null
        );

        ResponseEntity<BookResponse> createBookResponse = restTemplate.postForEntity(
                "/api/v1/books",
                bookRequest,
                BookResponse.class
        );

        Long bookId = createBookResponse.getBody().id();

        LoanRequest loanRequest = new LoanRequest(bookId, null);

        ResponseEntity<LoanResponse> response = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                LoanResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertEquals(bookId, response.getBody().bookId());
        assertEquals("Clean Code", response.getBody().bookTitle());
        assertNotNull(response.getBody().loanDate());
        assertNull(response.getBody().returnDate());
    }

    // POST /api/v1/loans -> 400 missing bookId
    @Test
    void createLoan_shouldReturn400WhenBookIdIsMissing() {
        LoanRequest loanRequest = new LoanRequest(null, null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Book id is required"));
    }
}
