package com.ordernest.inventory.service;

import com.ordernest.inventory.dto.ProductRequest;
import com.ordernest.inventory.dto.ProductResponse;
import com.ordernest.inventory.dto.StockUpdateRequest;
import com.ordernest.inventory.entity.Product;
import com.ordernest.inventory.exception.ConflictException;
import com.ordernest.inventory.exception.ResourceNotFoundException;
import com.ordernest.inventory.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        return mapToResponse(findProductById(id));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        String normalizedName = request.name().trim();
        String normalizedCurrency = request.currency().trim().toUpperCase();
        if (productRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException("Product name already exists");
        }

        Product product = new Product();
        product.setName(normalizedName);
        product.setPrice(request.price());
        product.setCurrency(normalizedCurrency);
        product.setAvailableQuantity(request.availableQuantity());
        product.setDescription(request.description().trim());

        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = findProductById(id);
        String normalizedName = request.name().trim();
        String normalizedCurrency = request.currency().trim().toUpperCase();

        boolean isNameChanged = !product.getName().equalsIgnoreCase(normalizedName);
        if (isNameChanged && productRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException("Product name already exists");
        }

        product.setName(normalizedName);
        product.setPrice(request.price());
        product.setCurrency(normalizedCurrency);
        product.setAvailableQuantity(request.availableQuantity());
        product.setDescription(request.description().trim());

        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProductStock(UUID id, StockUpdateRequest request) {
        Product product = findProductById(id);
        product.setAvailableQuantity(request.availableQuantity());
        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public void releaseProductStock(UUID productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            log.warn("Skipping inventory release due to invalid payload. productId={}, quantity={}", productId, quantity);
            return;
        }

        productRepository.findById(productId).ifPresentOrElse(
                product -> {
                    int current = product.getAvailableQuantity() == null ? 0 : product.getAvailableQuantity();
                    product.setAvailableQuantity(current + quantity);
                    productRepository.save(product);
                    log.info("Released inventory for failed payment. productId={}, quantity={}, newAvailableQuantity={}",
                            productId, quantity, product.getAvailableQuantity());
                },
                () -> log.warn("Skipping inventory release because product does not exist. productId={}", productId)
        );
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    private Product findProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCurrency(),
                product.getAvailableQuantity(),
                product.getDescription()
        );
    }
}
