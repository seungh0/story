package com.story.platform.core.support.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class ReactiveRedisConfig(
    private val reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
    private val objectMapper: ObjectMapper,
) {

    @Primary
    @Bean(DEFAULT_REACTIVE_REDIS_TEMPLATE)
    fun reactiveRedisTemplate(): ReactiveRedisTemplate<Any, Any?> {
        val serializationContext = RedisSerializationContext.newSerializationContext<Any, Any?>()
            .key(GenericJackson2JsonRedisSerializer(objectMapper))
            .value(GenericJackson2JsonRedisSerializer(objectMapper))
            .hashKey(StringRedisSerializer())
            .hashValue(GenericJackson2JsonRedisSerializer(objectMapper))
            .build()
        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, serializationContext)
    }

    @Primary
    @Bean(DEFAULT_REACTIVE_STRING_REDIS_TEMPLATE)
    fun stringReactiveRedisTemplate(): ReactiveRedisTemplate<String, String> {
        val serializationContext = RedisSerializationContext.newSerializationContext<String, String>()
            .key(StringRedisSerializer())
            .value(StringRedisSerializer())
            .hashKey(StringRedisSerializer())
            .hashValue(StringRedisSerializer())
            .build()
        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, serializationContext)
    }

    companion object {
        const val DEFAULT_REACTIVE_REDIS_TEMPLATE = "defaultReactiveRedisTemplate"
        const val DEFAULT_REACTIVE_STRING_REDIS_TEMPLATE = "defaultStringReactiveRedisTemplate"
    }

}
