package com.eolma.product.config;

import com.eolma.common.idempotency.IdempotencyChecker;
import com.eolma.common.idempotency.ProcessedEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdempotencyConfig {

    @Bean
    public IdempotencyChecker idempotencyChecker(ProcessedEventRepository processedEventRepository) {
        return new IdempotencyChecker(processedEventRepository);
    }
}
