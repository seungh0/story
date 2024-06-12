package com.story.core.support.circuitbreaker

enum class CircuitBreakerType(
    private val description: String,
) {

    DEFAULT(description = "DEFAULT"),
    REDIS_CACHE(description = "Redis Cache"),

}
