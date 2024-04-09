package com.story.core.infrastructure.curcuitbreaker

suspend fun <T> circuit(
    circuitBreakerType: CircuitBreakerType = CircuitBreakerType.DEFAULT,
    circuitBreaker: CircuitBreaker,
    block: suspend () -> T,
): Result<T> = circuitBreaker.run(circuitBreakerType, block)
