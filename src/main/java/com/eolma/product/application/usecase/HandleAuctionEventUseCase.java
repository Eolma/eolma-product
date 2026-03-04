package com.eolma.product.application.usecase;

import com.eolma.product.domain.model.Product;
import com.eolma.product.domain.model.ProductStatus;
import com.eolma.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.eolma.common.logging.StructuredLogger.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleAuctionEventUseCase {

    private final ProductService productService;

    @Transactional
    public void handleAuctionStarted(Long productId) {
        Product product = productService.findById(productId);
        product.changeStatus(ProductStatus.IN_AUCTION);
        productService.save(product);
        log.info("Product status changed to IN_AUCTION: {}", kv("productId", productId));
    }

    @Transactional
    public void handleAuctionCompleted(Long productId) {
        Product product = productService.findById(productId);
        product.changeStatus(ProductStatus.SOLD);
        productService.save(product);
        log.info("Product status changed to SOLD: {}", kv("productId", productId));
    }

    @Transactional
    public void handleAuctionFailed(Long productId) {
        Product product = productService.findById(productId);
        product.changeStatus(ProductStatus.ACTIVE);
        productService.save(product);
        log.info("Product status restored to ACTIVE: {}", kv("productId", productId));
    }
}
