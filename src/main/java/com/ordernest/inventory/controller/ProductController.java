package com.ordernest.inventory.controller;

import com.ordernest.inventory.dto.StockUpdateRequest;
import com.ordernest.inventory.dto.ProductResponse;
import com.ordernest.inventory.dto.PaginatedProductsResponse;
import com.ordernest.inventory.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/paginated")
    public ResponseEntity<PaginatedProductsResponse> getProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(productService.getProductsPage(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateProductStock(
            @PathVariable UUID id,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        return ResponseEntity.ok(productService.updateProductStock(id, request));
    }
}
