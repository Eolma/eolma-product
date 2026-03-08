package com.eolma.product.adapter.in.web;

import com.eolma.common.dto.PageResponse;
import com.eolma.product.adapter.in.web.dto.ProductResponse;
import com.eolma.product.adapter.in.web.dto.RegisterProductRequest;
import com.eolma.product.adapter.in.web.dto.UpdateProductRequest;
import com.eolma.product.application.usecase.ActivateProductUseCase;
import com.eolma.product.application.usecase.CancelProductUseCase;
import com.eolma.product.application.usecase.GetProductUseCase;
import com.eolma.product.application.usecase.RegisterProductUseCase;
import com.eolma.product.application.usecase.UpdateProductUseCase;
import com.eolma.product.domain.model.Category;
import com.eolma.product.domain.model.ProductStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final RegisterProductUseCase registerProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ActivateProductUseCase activateProductUseCase;
    private final CancelProductUseCase cancelProductUseCase;

    @PostMapping
    public ResponseEntity<ProductResponse> register(
            @RequestHeader("X-User-Id") String sellerId,
            @Valid @RequestBody RegisterProductRequest request) {
        var product = registerProductUseCase.execute(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.from(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getProductUseCase.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getProducts(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(getProductUseCase.findProducts(category, status, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<ProductResponse>> getMyProducts(
            @RequestHeader("X-User-Id") String sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(getProductUseCase.findMyProducts(sellerId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @RequestHeader("X-User-Id") String sellerId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(updateProductUseCase.execute(sellerId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-User-Id") String sellerId,
            @PathVariable Long id) {
        updateProductUseCase.delete(sellerId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @RequestHeader("X-User-Id") String sellerId,
            @PathVariable Long id) {
        activateProductUseCase.execute(sellerId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @RequestHeader("X-User-Id") String sellerId,
            @PathVariable Long id) {
        cancelProductUseCase.execute(sellerId, id);
        return ResponseEntity.ok().build();
    }
}
