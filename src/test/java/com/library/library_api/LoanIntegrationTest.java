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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "loan.create.delay-ms=200")
@ActiveProfiles("test")
public class LoanIntegrationTest {

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

    @BeforeEach
    void setUp() {
        restTemplate=testRestTemplate.withBasicAuth("admin", "admin123");
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

    @Test
    void createLoan_shouldReturn404WhenBookDoesNotExist() {
        LoanRequest loanRequest = new LoanRequest(9999L, null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Book with id 9999 not found"));
    }

    @Test
    void createLoan_shouldReturn409WhenBookIsAlreadyOnLoan() {
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

        ResponseEntity<LoanResponse> firstResponse = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                LoanResponse.class
        );

        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        ResponseEntity<String> secondResponse = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                String.class
        );

        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
        assertNotNull(secondResponse.getBody());
        assertTrue(secondResponse.getBody().contains("Book with id " + bookId + " is already on loan"));
    }

    @Test
    void getAllLoans_shouldReturnEmptyListWhenNoActiveLoansExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/loans",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"totalElements\":0"));
        assertTrue(response.getBody().contains("\"totalPages\":0"));
    }

    @Test
    void getAllLoans_shouldReturnActiveLoans() {
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

        ResponseEntity<LoanResponse> firstResponse = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                LoanResponse.class
        );

        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());


        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/loans",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"totalElements\":1"));
        assertTrue(response.getBody().contains("Clean Code"));
        assertTrue(response.getBody().contains("\"bookId\":" + bookId));
        assertTrue(response.getBody().contains("\"totalElements\":1"));
        assertTrue(response.getBody().contains("\"totalPages\":1"));
    }

    @Test
    void createLoan_shouldDemonstrateRaceConditionWhenTwoRequestsRunConcurrently() throws Exception {
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

        assertEquals(HttpStatus.CREATED, createBookResponse.getStatusCode());
        assertNotNull(createBookResponse.getBody());

        Long bookId = createBookResponse.getBody().id();
        LoanRequest loanRequest = new LoanRequest(bookId, null);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        List<ResponseEntity<String>> responses = new CopyOnWriteArrayList<>();
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        Runnable task = () -> {
            try {
                readyLatch.countDown();
                startLatch.await();

                ResponseEntity<String> response = restTemplate.postForEntity(
                        "/api/v1/loans",
                        loanRequest,
                        String.class
                );

                responses.add(response);
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                doneLatch.countDown();
            }
        };

        executorService.submit(task);
        executorService.submit(task);

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        assertEquals(2, responses.size());
        assertTrue(exceptions.isEmpty());

        long loanCount = loanRepository.count();

        System.out.println("Number of loans created: " + loanCount);
        responses.forEach(response ->
                System.out.println("Response status: " + response.getStatusCode() + ", body: " + response.getBody())
        );

        assertEquals(1, loanCount);

        long createdCount = responses.stream()
                .filter(response -> response.getStatusCode() == HttpStatus.CREATED)
                .count();

        long conflictCount = responses.stream()
                .filter(response -> response.getStatusCode() == HttpStatus.CONFLICT)
                .count();

        assertEquals(1, createdCount);
        assertEquals(1, conflictCount);

        assertTrue(responses.stream().anyMatch(response ->
                response.getBody() != null &&
                        response.getBody().contains("already on loan")));
    }

    @Test
    void createLoan_shouldHandle100ConcurrentRequestsWithoutDataCorruption() throws Exception {
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

        assertEquals(HttpStatus.CREATED, createBookResponse.getStatusCode());
        assertNotNull(createBookResponse.getBody());

        Long bookId = createBookResponse.getBody().id();
        LoanRequest loanRequest = new LoanRequest(bookId, null);

        int requestCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch readyLatch = new CountDownLatch(requestCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(requestCount);

        List<ResponseEntity<String>> responses = new CopyOnWriteArrayList<>();
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        Runnable task = () -> {
            try {
                readyLatch.countDown();
                startLatch.await();

                ResponseEntity<String> response = restTemplate.postForEntity(
                        "/api/v1/loans",
                        loanRequest,
                        String.class
                );

                responses.add(response);
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                doneLatch.countDown();
            }
        };

        for (int i = 0; i < requestCount; i++) {
            executorService.submit(task);
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        assertEquals(requestCount, responses.size());
        assertTrue(exceptions.isEmpty());

        long loanCount = loanRepository.count();
        assertEquals(1, loanCount);

        long createdCount = responses.stream()
                .filter(response -> response.getStatusCode() == HttpStatus.CREATED)
                .count();

        long conflictCount = responses.stream()
                .filter(response -> response.getStatusCode() == HttpStatus.CONFLICT)
                .count();

        assertEquals(1, createdCount);
        assertEquals(requestCount - 1, conflictCount);

        assertTrue(responses.stream().anyMatch(response ->
                response.getBody() != null &&
                        response.getBody().contains("already on loan")));
    }


    @Test
    void createLoan_shouldHandle100ConcurrentRequestsForDifferentBooks() throws Exception {
        int requestCount = 100;
        List<Long> bookIds = new CopyOnWriteArrayList<>();

        for (int i = 0; i < requestCount; i++) {
            BookRequest bookRequest = new BookRequest(
                    "Clean Code " + i,
                    "Robert C. Martin",
                    "978-0132350884-" + i,
                    2008,
                    null
            );

            ResponseEntity<BookResponse> createBookResponse = restTemplate.postForEntity(
                    "/api/v1/books",
                    bookRequest,
                    BookResponse.class
            );

            assertEquals(HttpStatus.CREATED, createBookResponse.getStatusCode());
            assertNotNull(createBookResponse.getBody());
            bookIds.add(createBookResponse.getBody().id());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch readyLatch = new CountDownLatch(requestCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(requestCount);

        List<ResponseEntity<String>> responses = new CopyOnWriteArrayList<>();
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        for (Long bookId : bookIds) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    LoanRequest loanRequest = new LoanRequest(bookId, null);

                    ResponseEntity<String> response = restTemplate.postForEntity(
                            "/api/v1/loans",
                            loanRequest,
                            String.class
                    );

                    responses.add(response);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        assertEquals(requestCount, responses.size());
        assertTrue(exceptions.isEmpty());

        long loanCount = loanRepository.count();
        assertEquals(requestCount, loanCount);

        long createdCount = responses.stream()
                .filter(response -> response.getStatusCode() == HttpStatus.CREATED)
                .count();

        long conflictCount = responses.stream()
                .filter(response -> response.getStatusCode() == HttpStatus.CONFLICT)
                .count();

        assertEquals(requestCount, createdCount);
        assertEquals(0, conflictCount);
    }

    @Test
    void returnLoanedBook_shouldReturn200AndSetReturnDate() {
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

        ResponseEntity<LoanResponse> createLoanResponse = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                LoanResponse.class
        );

        Long loanId = createLoanResponse.getBody().id();

        HttpEntity<Void> requestEntity = new HttpEntity<>(null);

        ResponseEntity<LoanResponse> response = restTemplate.exchange(
                "/api/v1/loans/" + loanId + "/return",
                HttpMethod.PATCH,
                requestEntity,
                LoanResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(loanId, response.getBody().id());
        assertEquals(bookId, response.getBody().bookId());
        assertNotNull(response.getBody().returnDate());
    }

    @Test
    void returnedBook_shouldBePossibleToLoanAgain() {
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

        assertEquals(HttpStatus.CREATED, createBookResponse.getStatusCode());
        assertNotNull(createBookResponse.getBody());

        Long bookId = createBookResponse.getBody().id();

        LoanRequest loanRequest = new LoanRequest(bookId, null);

        ResponseEntity<LoanResponse> firstLoanResponse = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                LoanResponse.class
        );

        assertEquals(HttpStatus.CREATED, firstLoanResponse.getStatusCode());
        assertNotNull(firstLoanResponse.getBody());

        Long firstLoanId = firstLoanResponse.getBody().id();

        ResponseEntity<LoanResponse> returnResponse = restTemplate.exchange(
                "/api/v1/loans/" + firstLoanId + "/return",
                HttpMethod.PATCH,
                new HttpEntity<>(null),
                LoanResponse.class
        );

        assertEquals(HttpStatus.OK, returnResponse.getStatusCode());
        assertNotNull(returnResponse.getBody());
        assertNotNull(returnResponse.getBody().returnDate());

        ResponseEntity<LoanResponse> secondLoanResponse = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                LoanResponse.class
        );

        assertEquals(HttpStatus.CREATED, secondLoanResponse.getStatusCode());
        assertNotNull(secondLoanResponse.getBody());
        assertNotEquals(firstLoanId, secondLoanResponse.getBody().id());
        assertEquals(bookId, secondLoanResponse.getBody().bookId());
        assertNull(secondLoanResponse.getBody().returnDate());
    }

    @Test
    void getLoanById_shouldReturn200AndCorrectLoan() {
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

        ResponseEntity<LoanResponse> createLoanResponse = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                LoanResponse.class
        );

        Long loanId = createLoanResponse.getBody().id();

        ResponseEntity<LoanResponse> response = restTemplate.getForEntity(
                "/api/v1/loans/" + loanId,
                LoanResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(loanId, response.getBody().id());
        assertEquals(bookId, response.getBody().bookId());
        assertEquals("Clean Code", response.getBody().bookTitle());
    }

    @Test
    void getLoanById_shouldReturn404WhenLoanDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/loans/9999",
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Loan with id 9999 not found"));
    }

    @Test
    void deleteLoan_shouldReturn204AndRemoveLoan() {
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

        ResponseEntity<LoanResponse> createLoanResponse = restTemplate.postForEntity(
                "/api/v1/loans",
                loanRequest,
                LoanResponse.class
        );

        Long loanId = createLoanResponse.getBody().id();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/loans/" + loanId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/api/v1/loans/" + loanId,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void deleteLoan_shouldReturn404WhenLoanDoesNotExist() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/loans/9999",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Loan with id 9999 not found"));
    }
}
