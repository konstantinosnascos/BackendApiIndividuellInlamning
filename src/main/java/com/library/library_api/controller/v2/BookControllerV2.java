package com.library.library_api.controller.v2;

import com.library.library_api.dto.VersionWrapper;
import com.library.library_api.dto.v2.BookResponseV2;
import com.library.library_api.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/books")
public class BookControllerV2 {
    private final BookService bookService;

    public BookControllerV2(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Get all books (v2)", description = "Return all books with availability in a wrapped response")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all books")
    @GetMapping
    public ResponseEntity<VersionWrapper<Page<BookResponseV2>>> getAllBooksV2(Pageable pageable) {
        Page<BookResponseV2> books = bookService.getAllBooksV2(pageable);
        VersionWrapper<Page<BookResponseV2>> response =
                new VersionWrapper<>(books, "v2");
        return ResponseEntity.ok(response);
    }
}
