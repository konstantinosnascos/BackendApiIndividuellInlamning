package com.library.library_api.dto.v1;


import io.swagger.v3.oas.annotations.media.Schema;

public record BookResponse(
        @Schema(description = "Book ID", example = "1")
        Long id,

        @Schema(description = "Book title", example = "Clean Code")
        String title,

        @Schema(description = "Book author", example = "Robert C. Martin")
        String author,

        @Schema(description = "ISBN number", example = "978-0132350884")
        String isbn,

        @Schema(description = "Year published", example = "2008")
        Integer publishedYear
) {}
