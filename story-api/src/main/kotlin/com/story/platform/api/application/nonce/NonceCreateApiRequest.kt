package com.story.platform.api.application.nonce

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive

data class NonceCreateApiRequest(
    @field:Positive
    @field:Max(value = 36_00) // 1H
    val expirationSeconds: Long = 60, // 1M
)
