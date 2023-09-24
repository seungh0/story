package com.story.platform.api.domain.nonce

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive

data class NonceCreateApiRequest(
    @field:Positive
    @field:Max(value = 864_00) // 1D
    val expirationSeconds: Long = 36_00, // 1H
)
