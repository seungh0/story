package com.story.platform.api.domain.component

import com.story.platform.core.domain.component.ComponentStatus

data class ComponentPatchApiRequest(
    val description: String?,
    val status: ComponentStatus?,
)
