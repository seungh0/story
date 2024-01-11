package com.story.api.application.component

import jakarta.validation.constraints.Size

data class ComponentCreateApiRequest(
    @field:Size(max = 300)
    val description: String = "",
)
