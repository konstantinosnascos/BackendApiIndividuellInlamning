package com.library.library_api.dto.v1;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
        @NotBlank(message = "Author name is required")
        Long id,
        String name,
        int bookCount
) {}
