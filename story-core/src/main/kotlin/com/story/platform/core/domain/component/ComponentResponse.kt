package com.story.platform.core.domain.component

import com.story.platform.core.common.model.AuditingTimeResponse

data class ComponentResponse(
    val componentId: String,
    val description: String,
    val status: ComponentStatus,
) : AuditingTimeResponse() {

    fun isActivated(): Boolean = status.isActivated()

    companion object {
        fun of(component: Component): ComponentResponse {
            val response = ComponentResponse(
                componentId = component.key.componentId,
                description = component.description,
                status = component.status,
            )
            response.from(component.auditingTime)
            return response
        }
    }

}
