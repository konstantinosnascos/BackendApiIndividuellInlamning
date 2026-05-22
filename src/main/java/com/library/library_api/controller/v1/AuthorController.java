package com.library.library_api.controller.v1;

import com.library.library_api.dto.v1.AuthorRequest;
import com.library.library_api.dto.v1.AuthorResponse;
import com.library.library_api.dto.v1.BookResponse;
import com.library.library_api.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
@Tag(name = "Authors", description = "Author management endpoints")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Create a new author", description = "Create a new author and return that author")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "author creation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest authorRequest) {
        AuthorResponse authorResponse = authorService.createAuthor(authorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorResponse);
    }

    @Operation(summary = "Get author by ID", description = "Returns an author by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author found"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable long id) {

        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    //http://localhost:8080/api/v1/authors/1/books?page=0&size=1 visar en bok
    @Operation(summary = "Get books by author ID", description = "Returns a list of books written by the specified author"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @GetMapping("/{id}/books")
    public ResponseEntity<Page<BookResponse>> getBooksByAuthorId(@PathVariable Long id,
                                                                 @PageableDefault(size=20, sort = "title")
                                                                 Pageable pageable) {
        return ResponseEntity.ok(
                authorService.getBooksByAuthorId(id, pageable));
    }

    //http://localhost:8080/api/v1/authors?page=0&size=2 för att testa pagination
    @Operation(summary = "Get all authors", description = "Returns a list of all authors")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all authors")
    @GetMapping
    public ResponseEntity<Page<AuthorResponse>> getAllAuthors(@PageableDefault(size=20, sort = "name")Pageable pageable) {
        return ResponseEntity.ok(authorService.getAllAuthors(pageable));
    }

    @Operation(summary = "Update author by ID", description = "Updates an existing author and returns the updated author")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Author updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest authorRequest) {

        return ResponseEntity.ok(authorService.updateAuthor(id, authorRequest));
    }

    @Operation(summary = "Delete author by ID", description = "Deletes an existing author")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
