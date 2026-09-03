package com.mnp.mobilenumberportability.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Hand-rolled instead of returning Spring Data's {@code Page} directly from the
 * controller — {@code PageImpl} doesn't serialize predictably with Jackson out
 * of the box, and this keeps the wire contract explicit and stable.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
