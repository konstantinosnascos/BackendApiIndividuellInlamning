package com.library.library_api.service;

import com.library.library_api.dto.v1.AuthorRequest;
import com.library.library_api.dto.v1.AuthorResponse;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.exception.AuthorHasBooksException;
import com.library.library_api.exception.AuthorNotFoundException;
import com.library.library_api.model.Author;
import com.library.library_api.model.Book;
import com.library.library_api.repository.AuthorRepository;
import com.library.library_api.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {

        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = new Author(request.name());
        Author savedAuthor = authorRepository.save(author);
        return toResponse(savedAuthor);
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks().size());
    }

    private BookResponse toBookResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getIsbn(),
                book.getPublishedYear());
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository
                .findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
        return toResponse(author);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getBooksByAuthorId(
            Long id,
            Pageable pageable) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        return bookRepository.findByAuthor(author, pageable)
                .map(this::toBookResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuthorResponse> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        author.setName(request.name());

        Author savedAuthor = authorRepository.save(author);
        return toResponse(savedAuthor);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        if (!author.getBooks().isEmpty()) {
            throw new AuthorHasBooksException(id);
        }

        authorRepository.delete(author);
    }
}