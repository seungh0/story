package com.story.api.application.nonce

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive

data class NonceCreateRequest(
    @field:Positive
    @field:Max(value = 36_00) // 1H
    val expirationSeconds: Long = 60, // 1M
)
