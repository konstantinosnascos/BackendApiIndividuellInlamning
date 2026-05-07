package com.library.library_api.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
        @Schema(description = "Author name", example = "Robert C. Martin")
        @NotBlank(message = "Author name is required") String name
) {}
