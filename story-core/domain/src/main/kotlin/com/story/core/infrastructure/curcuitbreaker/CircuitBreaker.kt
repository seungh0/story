package com.story.core.infrastructure.curcuitbreaker

interface CircuitBreaker {

    suspend fun <T> run(circuitBreakerType: CircuitBreakerType, block: suspend () -> T): Result<T>

}
