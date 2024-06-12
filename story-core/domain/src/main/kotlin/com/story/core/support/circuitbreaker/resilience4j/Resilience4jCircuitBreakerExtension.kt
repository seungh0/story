package com.story.core.support.circuitbreaker.resilience4j

import com.story.core.support.circuitbreaker.CircuitOpenException
import io.github.resilience4j.circuitbreaker.CallNotPermittedException

internal fun Throwable.turnToOpenExceptionIfOpen(): Throwable = when (this) {
    is CallNotPermittedException -> CircuitOpenException(message = this.message ?: "Circuit is open")
    else -> this
}
