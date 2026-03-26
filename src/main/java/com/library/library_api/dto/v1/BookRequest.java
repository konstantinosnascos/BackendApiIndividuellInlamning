package com.library.library_api.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @Schema(description = "Book title", example = "Clean Code")
        @NotBlank(message = "Title is required") String title,

        @Schema(description = "Book author", example = "Robert C. Martin")
        @NotBlank(message = "Author is required") String author,

        @Schema(description = "ISBN number", example = "978-0132350884")
        String isbn,

        @Schema(description = "Year published", example = "2008")
        Integer publishedYear,
        Long authorId
) {}