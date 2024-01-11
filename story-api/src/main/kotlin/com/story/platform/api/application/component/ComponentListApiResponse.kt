package com.story.platform.api.application.component

import com.story.platform.core.common.model.Slice
import com.story.platform.core.common.model.dto.CursorResponse
import com.story.platform.core.domain.component.ComponentResponse

data class ComponentListApiResponse(
    val components: List<ComponentApiResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(components: Slice<ComponentResponse, String>) = ComponentListApiResponse(
            components = components.data.map { component -> ComponentApiResponse.of(component) },
            cursor = components.cursor,
        )
    }

}