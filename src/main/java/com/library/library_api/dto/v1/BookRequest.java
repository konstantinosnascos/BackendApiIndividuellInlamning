package com.library.library_api.dto.v1;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Author is required") String author,
        String isbn,
        Integer publishedYear
) {}