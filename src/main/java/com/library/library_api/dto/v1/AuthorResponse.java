package com.library.library_api.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorResponse(
        @Schema(description = "Author ID", example = "1") Long id,

        @Schema(description = "Author name", example = "") String name,

        @Schema(description = "Number of books", example = "10") int bookCount
) {}
