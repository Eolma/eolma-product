package com.eolma.product.config;

import com.eolma.common.redis.EolmaRedisTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public EolmaRedisTemplate eolmaRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        return new EolmaRedisTemplate(stringRedisTemplate);
    }
}
