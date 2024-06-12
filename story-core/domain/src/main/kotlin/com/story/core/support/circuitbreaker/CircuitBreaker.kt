package com.story.core.support.circuitbreaker

interface CircuitBreaker {

    suspend fun <T> run(circuitBreakerType: CircuitBreakerType, block: suspend () -> T): Result<T>

}
