package com.eolma.product.application.usecase;

import com.eolma.common.event.DomainEvent;
import com.eolma.common.event.EventType;
import com.eolma.common.event.payload.ProductActivatedEvent;
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
public class ActivateProductUseCase {

    private final ProductService productService;
    private final EventPublisher eventPublisher;

    @Transactional
    public void execute(String sellerId, Long productId) {
        Product product = productService.findById(productId);
        product.validateOwnership(sellerId);
        product.activate();
        productService.save(product);

        ProductActivatedEvent payload = new ProductActivatedEvent(
                product.getId(),
                product.getSellerId(),
                product.getTitle(),
                product.getDescription(),
                product.getCategory().name(),
                product.getConditionGrade().name(),
                product.getStartingPrice(),
                product.getInstantPrice(),
                product.getReservePrice(),
                product.getMinBidUnit(),
                product.getEndType().name(),
                product.getDurationHours(),
                product.getMaxBidCount(),
                product.getImageUrls()
        );

        DomainEvent<ProductActivatedEvent> event = DomainEvent.create(
                EventType.PRODUCT_ACTIVATED,
                "product-service",
                String.valueOf(product.getId()),
                "Product",
                payload
        );

        eventPublisher.publish(event);
        log.info("Product activated: {}", kv("productId", productId));
    }
}
