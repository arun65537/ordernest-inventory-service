package com.ordernest.inventory.dto;

import java.util.List;

public record PaginatedProductsResponse(
        List<ProductResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
