package com.ming.mingcommerce.util;

import org.springframework.data.domain.Page;

import java.util.List;


public record PagingObject<T>(
        int totalPages,
        List<T> content,
        boolean first,
        boolean last,
        long totalElements
) {

    PagingObject(Page<T> defaultPage) {
        this(defaultPage.getTotalPages(),
                defaultPage.getContent(),
                defaultPage.isFirst(),
                defaultPage.isLast(),
                defaultPage.getTotalElements()
        );
    }

    public static <T> PagingObject<T> of(Page<T> defaultPage) {
        return new PagingObject<>(defaultPage);
    }
}
