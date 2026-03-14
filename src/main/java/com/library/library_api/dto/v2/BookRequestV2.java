package com.library.library_api.dto.v2;

public record BookRequestV2(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publishedYear,
        boolean available
) {}