package com.library.library_api.dto.v1;

public record AuthorResponse(
        Long id,
        String name,
        int bookCount
) {}
