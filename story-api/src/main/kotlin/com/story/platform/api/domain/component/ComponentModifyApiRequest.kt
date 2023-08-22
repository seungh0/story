package com.story.platform.api.domain.component

import com.story.platform.core.domain.component.ComponentStatus
import jakarta.validation.constraints.Size

data class ComponentModifyApiRequest(
    @field:Size(max = 300)
    val description: String?,
    val status: ComponentStatus?,
)
