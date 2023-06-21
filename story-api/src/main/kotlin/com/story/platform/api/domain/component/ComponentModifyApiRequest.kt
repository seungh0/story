package com.story.platform.api.domain.component

import com.story.platform.core.domain.component.ComponentStatus

data class ComponentModifyApiRequest(
    val description: String?,
    val status: ComponentStatus?,
)
