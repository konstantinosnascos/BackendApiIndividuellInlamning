package com.library.library_api;


import com.library.library_api.dto.v1.BookRequest;
import com.library.library_api.dto.v1.BookResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createBook_shouldReturn201AndSavedBook() {
        BookRequest request = new BookRequest(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008
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
                2008
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
}
