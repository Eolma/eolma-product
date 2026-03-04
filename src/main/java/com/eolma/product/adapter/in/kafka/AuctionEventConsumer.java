package com.eolma.product.adapter.in.kafka;

import com.eolma.common.event.DomainEvent;
import com.eolma.common.event.EventType;
import com.eolma.common.idempotency.IdempotencyChecker;
import com.eolma.product.application.usecase.HandleAuctionEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.eolma.common.logging.StructuredLogger.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventConsumer {

    private final HandleAuctionEventUseCase handleAuctionEventUseCase;
    private final IdempotencyChecker idempotencyChecker;

    @KafkaListener(
            topics = "eolma.auction.events",
            groupId = "product-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, DomainEvent<?>> record, Acknowledgment ack) {
        DomainEvent<?> event = record.value();
        String eventType = event.type();

        log.info("Received auction event: {}, {}", kv("type", eventType), kv("eventId", event.id()));

        idempotencyChecker.processOnce(event.id(), () -> {
            Long productId = extractProductId(event);
            switch (eventType) {
                case EventType.AUCTION_STARTED -> handleAuctionEventUseCase.handleAuctionStarted(productId);
                case EventType.AUCTION_COMPLETED -> handleAuctionEventUseCase.handleAuctionCompleted(productId);
                case EventType.AUCTION_FAILED -> handleAuctionEventUseCase.handleAuctionFailed(productId);
                default -> log.warn("Unknown event type: {}", kv("type", eventType));
            }
        });

        ack.acknowledge();
    }

    private Long extractProductId(DomainEvent<?> event) {
        Object payload = event.payload();
        if (payload instanceof Map<?, ?> map) {
            Object productId = map.get("productId");
            if (productId instanceof Number number) {
                return number.longValue();
            }
        }
        throw new IllegalArgumentException("Cannot extract productId from event: " + event.id());
    }
}
