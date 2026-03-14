package com.library.library_api.dto.v2;

public record BookResponseV2(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publishedYear,
        boolean available
) {}