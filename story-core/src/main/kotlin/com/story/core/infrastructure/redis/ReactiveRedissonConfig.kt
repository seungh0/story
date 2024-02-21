package com.story.core.infrastructure.redis

import org.redisson.Redisson
import org.redisson.api.RedissonReactiveClient
import org.redisson.config.Config
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReactiveRedissonConfig(
    private val redisProperties: RedisProperties,
) {

    @Bean
    fun redissonReactiveClient(): RedissonReactiveClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://" + redisProperties.host + ":" + redisProperties.port)
            .setConnectionMinimumIdleSize(5)
            .setConnectionPoolSize(5)
        return Redisson.create(config).reactive()
    }

}
