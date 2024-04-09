package com.story.core.infrastructure.curcuitbreaker.resilience4j

import com.story.core.infrastructure.curcuitbreaker.CircuitOpenException
import io.github.resilience4j.circuitbreaker.CallNotPermittedException

internal fun Throwable.turnToOpenExceptionIfOpen(): Throwable = when (this) {
    is CallNotPermittedException -> CircuitOpenException(message = this.message ?: "Circuit is open")
    else -> this
}
