package com.foodtoeat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            org.springframework.boot.data.redis.autoconfigure.DataRedisProperties redisProperties) {

        log.info("Redis host: {}", redisProperties.getHost());
        log.info("Redis port: {}", redisProperties.getPort());
        log.info("Redis database: {}", redisProperties.getDatabase());
        log.info("Redis password: {}", redisProperties.getPassword());
        log.info("Redis password configured: {}",
                redisProperties.getPassword() != null);


        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
