package com.story.api.application.apikey

import com.story.core.domain.apikey.ApiKeyStatus
import jakarta.validation.constraints.Size

data class ApiKeyModifyRequest(
    @field:Size(max = 300)
    val description: String? = null,
    val status: ApiKeyStatus? = null,
)
