package com.eolma.product.application.usecase;

import com.eolma.common.event.DomainEvent;
import com.eolma.common.event.EventType;
import com.eolma.common.event.payload.ProductCancelledEvent;
import com.eolma.product.application.port.out.EventPublisher;
import com.eolma.product.domain.model.Product;
import com.eolma.product.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.eolma.common.logging.StructuredLogger.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelProductUseCase {

    private final ProductService productService;
    private final EventPublisher eventPublisher;

    @Transactional
    public void execute(Long sellerId, Long productId) {
        Product product = productService.findById(productId);
        product.validateOwnership(sellerId);
        product.cancel();
        productService.save(product);

        ProductCancelledEvent payload = new ProductCancelledEvent(
                product.getId(),
                product.getSellerId(),
                "Cancelled by seller"
        );

        DomainEvent<ProductCancelledEvent> event = DomainEvent.create(
                EventType.PRODUCT_CANCELLED,
                "product-service",
                String.valueOf(product.getId()),
                "Product",
                payload
        );

        eventPublisher.publish(event);
        log.info("Product cancelled: {}", kv("productId", productId));
    }
}
