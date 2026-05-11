package org.wavemoney.payment.api.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory
    ) {

//        RedisCacheConfiguration config =
//                RedisCacheConfiguration.defaultCacheConfig()
//                        .entryTtl(Duration.ofMinutes(10));

//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(config)
//                .build();
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put("wallets",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(5)));

        configs.put("users",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(5)));

                return RedisCacheManager.builder(redisConnectionFactory)
                        .withInitialCacheConfigurations(configs)
                        .build();
    }
}