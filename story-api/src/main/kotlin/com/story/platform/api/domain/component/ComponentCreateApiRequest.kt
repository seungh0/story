package com.story.platform.api.domain.component

import jakarta.validation.constraints.Size

data class ComponentCreateApiRequest(
    @field:Size(max = 300)
    val description: String = "",
)
