package com.story.platform.api.domain.component

import com.story.platform.core.domain.component.ComponentStatus

data class ComponentGetApiRequest(
    val filterStatus: ComponentStatus? = ComponentStatus.ENABLED,
)
