package com.library.library_api.service;


import com.library.library_api.dto.v1.BookRequest;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.dto.v2.BookResponseV2;
import com.library.library_api.exception.BookAlreadyLoanedOutException;
import com.library.library_api.exception.BookNotFoundException;
import com.library.library_api.model.Book;
import com.library.library_api.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse createBook(BookRequest bookRequest) {
        Book book = new Book(
                bookRequest.title(),
                bookRequest.author(),
                bookRequest.isbn(),
                bookRequest.publishedYear(),
                true);

        Book savedBook = bookRepository.save(book);
        return toResponse(savedBook);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BookResponse toResponse(Book book){
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublishedYear());
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookAlreadyLoanedOutException(id));
        return toResponse(book);
    }

    public List<BookResponseV2> getAllBooksV2() {
        return bookRepository.findAll()
                .stream()
                .map(this::toResponseV2)
                .collect(Collectors.toList());
    }

    private BookResponseV2 toResponseV2(Book book) {
        return new BookResponseV2(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.isAvailable()
                );
    }
}
