package com.library.library_api;


import com.library.library_api.dto.v1.AuthorRequest;
import com.library.library_api.dto.v1.AuthorResponse;
import com.library.library_api.dto.v1.BookRequest;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.repository.AuthorRepository;
import com.library.library_api.repository.BookRepository;
import com.library.library_api.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthorIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TestRestTemplate testRestTemplateExtra;


    @BeforeEach
    void setUp() {
        restTemplate = testRestTemplate.withBasicAuth("admin", "admin123");
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    void createAuthor_shouldReturn201AndSavedAuthor() {
        AuthorRequest request = new AuthorRequest("Robert C. Martin");

        ResponseEntity<AuthorResponse> response = restTemplate.postForEntity(
                "/api/v1/authors",
                request,
                AuthorResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertEquals("Robert C. Martin", response.getBody().name());
        assertEquals(0, response.getBody().bookCount());
    }

    @Test
    void getAuthorById_shouldReturn200AndCorrectAuthor() {
        AuthorRequest request = new AuthorRequest("Martin Fowler");

        ResponseEntity<AuthorResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/authors",
                request,
                AuthorResponse.class
        );

        Long authorId = createResponse.getBody().id();

        ResponseEntity<AuthorResponse> response = restTemplate.getForEntity(
                "/api/v1/authors/" + authorId,
                AuthorResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authorId, response.getBody().id());
        assertEquals("Martin Fowler", response.getBody().name());
        assertEquals(0, response.getBody().bookCount());
    }

    @Test
    void getAuthorById_shouldReturn404WhenAuthorDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/authors/9999",
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Author with id 9999 not found"));
    }

    @Test
    void getBooksByAuthorId_shouldReturn404WhenAuthorDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/authors/9999/books",
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Author with id 9999 not found"));
    }

    @Test
    void createAuthor_shouldReturn400WhenNameIsBlank() {
        AuthorRequest request = new AuthorRequest("");
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/authors",
                request,
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Author name is required"));
    }

    @Test
    void getBooksByAuthorId_shouldReturnEmptyListWhenAuthorHasNoBooks() {
        AuthorRequest request = new AuthorRequest("Martin Fowler");
        ResponseEntity<AuthorResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/authors",
                request,
                AuthorResponse.class
        );
        Long authorId = createResponse.getBody().id();

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/authors/" + authorId + "/books",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"totalElements\":0"));
        assertTrue(response.getBody().contains("\"totalPages\":0"));
    }

    @Test
    void getBooksByAuthorId_shouldReturnBooksForAuthor() {
        AuthorRequest authorRequest = new AuthorRequest("Martin Fowler");

        ResponseEntity<AuthorResponse> createAuthorResponse = restTemplate.postForEntity(
                "/api/v1/authors",
                authorRequest,
                AuthorResponse.class
        );

        Long authorId = createAuthorResponse.getBody().id();

        BookRequest bookRequest = new BookRequest(
                "Refactoring",
                "Martin Fowler",
                "978-0201485677",
                1999,
                authorId
        );

        ResponseEntity<BookResponse> createBookResponse = restTemplate.postForEntity(
                "/api/v1/books",
                bookRequest,
                BookResponse.class
        );

        assertEquals(HttpStatus.CREATED, createBookResponse.getStatusCode());
        assertNotNull(createBookResponse.getBody());

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/authors/" + authorId + "/books",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"title\":\"Refactoring\""));
        assertTrue(response.getBody().contains("\"author\":\"Martin Fowler\""));
    }

    @Test
    void updateAuthor_shouldReturn200AndUpdatedAuthor() {
        AuthorRequest createRequest = new AuthorRequest("Robert C. Martin");

        ResponseEntity<AuthorResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/authors",
                createRequest,
                AuthorResponse.class
        );

        Long authorId = createResponse.getBody().id();

        AuthorRequest updateRequest = new AuthorRequest("Uncle Bob");

        ResponseEntity<AuthorResponse> response = restTemplate.exchange(
                "/api/v1/authors/" + authorId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                AuthorResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authorId, response.getBody().id());
        assertEquals("Uncle Bob", response.getBody().name());
    }

    @Test
    void deleteAuthor_shouldReturn204WhenAuthorHasNoBooks() {
        AuthorRequest createRequest = new AuthorRequest("Author Without Books");

        ResponseEntity<AuthorResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/authors",
                createRequest,
                AuthorResponse.class
        );

        Long authorId = createResponse.getBody().id();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/authors/" + authorId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/api/v1/authors/" + authorId,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void deleteAuthor_shouldReturn400WhenAuthorHasBooks() {
        AuthorRequest authorRequest = new AuthorRequest("Martin Fowler");

        ResponseEntity<AuthorResponse> createAuthorResponse = restTemplate.postForEntity(
                "/api/v1/authors",
                authorRequest,
                AuthorResponse.class
        );

        Long authorId = createAuthorResponse.getBody().id();

        BookRequest bookRequest = new BookRequest(
                "Refactoring",
                "Martin Fowler",
                "978-0201485677",
                1999,
                authorId
        );

        ResponseEntity<BookResponse> createBookResponse = restTemplate.postForEntity(
                "/api/v1/books",
                bookRequest,
                BookResponse.class
        );

        assertEquals(HttpStatus.CREATED, createBookResponse.getStatusCode());

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/api/v1/authors/" + authorId,
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertTrue(deleteResponse.getBody().contains("cannot be deleted because they have books"));
    }

    @Test
    void createAuthor_shoulFailWithoutLoginCredentialsAndReturn401() {
        AuthorRequest request = new AuthorRequest("Robert C. Martin");

        ResponseEntity<AuthorResponse> response = testRestTemplateExtra.postForEntity(
                "/api/v1/authors",
                request,
                AuthorResponse.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteAuthor_shoulFailWithoutLoginCredentialsAndReturn401() {
        AuthorRequest request = new AuthorRequest("Robert C. Martin");

        ResponseEntity<AuthorResponse> response = testRestTemplateExtra.withBasicAuth("user", "password").postForEntity(
                "/api/v1/authors",
                request,
                AuthorResponse.class
        );

        Long authorId = response.getBody().id();

        ResponseEntity<Void> deleteResponse = testRestTemplateExtra.exchange(
                "/api/v1/authors/" + authorId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, deleteResponse.getStatusCode());
        assertNull(deleteResponse.getBody());
    }

    @Test
    void deleteAuthor_shoulFailWithUserLoginCredentialsAndReturn403() {
        AuthorRequest request = new AuthorRequest("Robert C. Martin");

        ResponseEntity<AuthorResponse> response = testRestTemplateExtra.withBasicAuth("user", "password").postForEntity(
                "/api/v1/authors",
                request,
                AuthorResponse.class
        );

        Long authorId = response.getBody().id();

        ResponseEntity<Void> deleteResponse = testRestTemplateExtra.withBasicAuth("user", "password").exchange(
                "/api/v1/authors/" + authorId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.FORBIDDEN, deleteResponse.getStatusCode());
        assertNull(deleteResponse.getBody());
    }
}

