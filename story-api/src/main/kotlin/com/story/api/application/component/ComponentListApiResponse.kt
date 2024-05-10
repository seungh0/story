package com.story.api.application.component

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.domain.component.Component

data class ComponentListApiResponse(
    val components: List<ComponentApiResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(components: Slice<Component, String>) = ComponentListApiResponse(
            components = components.data.map { component -> ComponentApiResponse.of(component) },
            cursor = components.cursor,
        )
    }

}
