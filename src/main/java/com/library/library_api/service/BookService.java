package com.library.library_api.service;


import com.library.library_api.dto.v1.BookRequest;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.dto.v2.BookResponseV2;
import com.library.library_api.exception.AuthorNotFoundException;
import com.library.library_api.exception.BookAlreadyLoanedOutException;
import com.library.library_api.exception.BookNotFoundException;
import com.library.library_api.model.Author;
import com.library.library_api.model.Book;
import com.library.library_api.repository.AuthorRepository;
import com.library.library_api.repository.BookRepository;
import com.library.library_api.repository.LoanRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.loanRepository = loanRepository;
    }

    //@CacheEvict(value = {"books", "booksV2", "book"}, allEntries = true)
    public BookResponse createBook(BookRequest bookRequest) {
        Author author = resolveAuthor(bookRequest);

        Book book = new Book(
                bookRequest.title(),
                author,
                bookRequest.isbn(),
                bookRequest.publishedYear(),
                true
        );

        Book savedBook = bookRepository.save(book);
        return toResponse(savedBook);
    }

    //@Cacheable("books")
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private BookResponse toResponse(Book book){
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getIsbn(),
                book.getPublishedYear());
    }

    //@Cacheable(value = "book", key = "#id")
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        return toResponse(book);
    }

    //@Cacheable("booksV2")
    public Page<BookResponseV2> getAllBooksV2(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::toResponseV2);
    }

    private BookResponseV2 toResponseV2(Book book) {

        boolean available = loanRepository.findByBookIdAndReturnDateIsNull(book.getId()).isEmpty();
        return new BookResponseV2(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getIsbn(),
                book.getPublishedYear(),
                available
                );
    }

    //@CacheEvict(value = {"books", "booksV2", "book"}, allEntries = true)
    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        Author author = resolveAuthor(bookRequest);

        book.setTitle(bookRequest.title());
        book.setAuthor(author);
        book.setIsbn(bookRequest.isbn());
        book.setPublishedYear(bookRequest.publishedYear());

        Book savedBook = bookRepository.save(book);
        return toResponse(savedBook);
    }

    //@CacheEvict(value = {"books", "booksV2", "book"}, allEntries = true)
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        loanRepository.findByBookIdAndReturnDateIsNull(id)
                .ifPresent(loan -> {
                    throw new BookAlreadyLoanedOutException(id);
                });

        bookRepository.delete(book);
    }

    private Author resolveAuthor(BookRequest bookRequest) {
        if (bookRequest.authorId() != null) {
            return authorRepository.findById(bookRequest.authorId())
                    .orElseThrow(() -> new AuthorNotFoundException(bookRequest.authorId()));
        }

        if (bookRequest.author() == null || bookRequest.author().isBlank()) {
            throw new IllegalArgumentException("Author or authorId is required");
        }

        return authorRepository.findByNameIgnoreCase(bookRequest.author())
                .orElseGet(() -> authorRepository.save(new Author(bookRequest.author())));
    }
}
