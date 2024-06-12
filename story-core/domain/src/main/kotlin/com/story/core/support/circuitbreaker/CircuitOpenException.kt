package com.story.core.support.circuitbreaker

data class CircuitOpenException(
    override val message: String,
) : RuntimeException(message)
