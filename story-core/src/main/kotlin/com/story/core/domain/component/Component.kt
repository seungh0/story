package com.story.core.domain.component

import com.story.core.common.model.dto.AuditingTimeResponse
import com.story.core.domain.component.storage.ComponentEntity

data class Component(
    val componentId: String,
    val description: String,
    val status: ComponentStatus,
) : AuditingTimeResponse() {

    fun isActivated(): Boolean = status.isActivated()

    companion object {
        fun of(component: ComponentEntity): Component {
            val response = Component(
                componentId = component.key.componentId,
                description = component.description,
                status = component.status,
            )
            response.setAuditingTime(component.auditingTime)
            return response
        }
    }

}
