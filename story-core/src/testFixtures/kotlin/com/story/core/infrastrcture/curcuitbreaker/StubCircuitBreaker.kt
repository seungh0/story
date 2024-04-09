package com.story.core.infrastrcture.curcuitbreaker

import com.story.core.infrastructure.curcuitbreaker.CircuitBreaker
import com.story.core.infrastructure.curcuitbreaker.CircuitBreakerType
import com.story.core.infrastructure.curcuitbreaker.CircuitOpenException

class StubCircuitBreaker(
    var circuitOpen: Boolean = false,
) : CircuitBreaker {

    override suspend fun <T> run(circuitBreakerType: CircuitBreakerType, block: suspend () -> T): Result<T> {
        if (circuitOpen) {
            return Result.failure(exception = CircuitOpenException("서킷이 오픈되어 있습니다"))
        }

        return try {
            Result.success(block.invoke())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

}
