package com.story.core.infrastructure.curcuitbreaker

data class CircuitOpenException(
    override val message: String,
) : RuntimeException(message)
