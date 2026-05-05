package com.library.library_api.controller.v1;

import com.library.library_api.dto.v1.AuthorRequest;
import com.library.library_api.dto.v1.AuthorResponse;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest authorRequest) {
        AuthorResponse authorResponse = authorService.createAuthor(authorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable long id) {

        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<BookResponse>> getBooksByAuthorId(@PathVariable long id) {
        return ResponseEntity.ok(authorService.getBooksByAuthorId(id));
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest authorRequest) {

        return ResponseEntity.ok(authorService.updateAuthor(id, authorRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
