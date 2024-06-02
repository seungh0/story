package com.story.core.domain.component

import com.story.core.common.model.dto.AuditingTimeResponse

data class Component(
    val componentId: String,
    val description: String,
    val status: ComponentStatus,
) : AuditingTimeResponse() {

    fun isActivated(): Boolean = status.isActivated()

}
