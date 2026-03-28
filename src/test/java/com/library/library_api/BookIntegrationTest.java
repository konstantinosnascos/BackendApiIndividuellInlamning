package com.library.library_api;


import com.library.library_api.dto.v1.BookRequest;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.repository.AuthorRepository;
import com.library.library_api.repository.BookRepository;
import com.library.library_api.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookIntegrationTest {

    // POST /api/v1/books -> 201 create book
    // POST /api/v1/books -> 400 invalid input
    // POST /api/v1/books -> 404 authorId does not exist

    // GET /api/v1/books -> 200 list books
    // GET /api/v1/books -> 200 empty list when no books exist
    // GET /api/v1/books/{id} -> 200 existing book
    // GET /api/v1/books/{id} -> 404 missing book


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

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createBook_shouldReturn201AndSavedBook() {
        BookRequest request = new BookRequest(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                null
        );

        ResponseEntity<BookResponse> response = restTemplate.postForEntity(
                "/api/v1/books",
                request,
                BookResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertEquals("Clean Code", response.getBody().title());
        assertEquals("Robert C. Martin", response.getBody().author());
        assertEquals("978-0132350884", response.getBody().isbn());
        assertEquals(2008, response.getBody().publishedYear());
    }

    @Test
    void getBookById_shouldReturn200AndCorrectBook() {
        BookRequest request = new BookRequest(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                null
        );
        ResponseEntity<BookResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/books",
                request,
                BookResponse.class
        );
        Long bookId = createResponse.getBody().id();

        ResponseEntity<BookResponse> response = restTemplate.getForEntity(
                "/api/v1/books/" + bookId,
                BookResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookId, response.getBody().id());
        assertEquals("Clean Code", response.getBody().title());
        assertEquals("Robert C. Martin", response.getBody().author());
    }

    @Test
    void getBookById_shouldReturn404WhenBookDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/books/9999",
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Book with id 9999 not found"));
    }

    @Test
    void createBook_shouldReturn400WhenTitleIsBlank() {
        BookRequest request = new BookRequest(
                "",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                null
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/books",
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Title is required"));
    }

    @Test
    void getAllBooksV2_shouldReturnWrappedBooksWithVersion() {
        BookRequest request = new BookRequest(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                null
        );

        ResponseEntity<BookResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/books",
                request,
                BookResponse.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v2/books",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"version\":\"v2\""));
        assertTrue(response.getBody().contains("Clean Code"));
        assertTrue(response.getBody().contains("\"available\":true"));
    }
}
