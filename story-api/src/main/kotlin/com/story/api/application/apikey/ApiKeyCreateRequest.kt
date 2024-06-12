package com.story.api.application.apikey

import jakarta.validation.constraints.Size

data class ApiKeyCreateRequest(
    @field:Size(max = 300)
    val description: String = "",
)