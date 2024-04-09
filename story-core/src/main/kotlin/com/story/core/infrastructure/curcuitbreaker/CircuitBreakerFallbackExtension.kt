package com.story.core.infrastructure.curcuitbreaker

fun <T> Result<T>.fallback(function: (throwable: Throwable?) -> T): Result<T> = when (this.isSuccess) {
    true -> this
    false -> runCatching { function(this.exceptionOrNull()) }
}

fun <T> Result<T>.fallbackIfOpen(function: (throwable: Throwable?) -> T): Result<T> = when (this.exceptionOrNull()) {
    is CircuitOpenException -> runCatching { function(this.exceptionOrNull()) }
    else -> this
}
