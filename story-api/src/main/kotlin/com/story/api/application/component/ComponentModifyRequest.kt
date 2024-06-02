package com.story.api.application.component

import com.story.core.domain.component.ComponentStatus
import jakarta.validation.constraints.Size

data class ComponentModifyRequest(
    @field:Size(max = 300)
    val description: String?,
    val status: ComponentStatus?,
)
