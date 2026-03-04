package com.eolma.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.eolma.product.adapter.out.persistence")
public class JpaConfig {
}
