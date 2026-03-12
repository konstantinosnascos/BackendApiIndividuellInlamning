package com.library.library_api.dto.v1;


public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publishedYear
) {}