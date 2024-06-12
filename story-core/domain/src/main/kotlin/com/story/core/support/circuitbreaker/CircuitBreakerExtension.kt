package com.story.core.support.circuitbreaker

suspend fun <T> circuit(
    circuitBreakerType: CircuitBreakerType = CircuitBreakerType.DEFAULT,
    circuitBreaker: CircuitBreaker,
    block: suspend () -> T,
): Result<T> = circuitBreaker.run(circuitBreakerType, block)
