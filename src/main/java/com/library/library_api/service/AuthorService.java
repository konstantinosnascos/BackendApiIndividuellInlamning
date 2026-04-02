package com.library.library_api.service;

import com.library.library_api.dto.v1.AuthorRequest;
import com.library.library_api.dto.v1.AuthorResponse;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.exception.AuthorNotFoundException;
import com.library.library_api.model.Author;
import com.library.library_api.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {

        this.authorRepository = authorRepository;
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

    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository
                .findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
        return toResponse(author);
    }

    public List<BookResponse> getBooksByAuthorId(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));

        return author.getBooks()
                .stream()
                .map(book -> new BookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPublishedYear()
                ))
                .collect(Collectors.toList());
    }
}