package com.story.core.infrastructure.curcuitbreaker

enum class CircuitBreakerType(
    private val description: String,
) {

    DEFAULT(description = "DEFAULT"),
    REDIS_CACHE(description = "Redis Cache"),

}
