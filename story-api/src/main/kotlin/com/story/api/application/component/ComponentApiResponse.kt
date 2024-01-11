package com.story.api.application.component

import com.story.core.common.model.dto.AuditingTimeResponse
import com.story.core.domain.component.ComponentResponse
import com.story.core.domain.component.ComponentStatus

data class ComponentApiResponse(
    val componentId: String,
    val description: String,
    val status: ComponentStatus,
) : AuditingTimeResponse() {

    companion object {
        fun of(component: ComponentResponse): ComponentApiResponse {
            val response = ComponentApiResponse(
                componentId = component.componentId,
                description = component.description,
                status = component.status,
            )
            response.setAuditingTime(component)
            return response
        }
    }

}
