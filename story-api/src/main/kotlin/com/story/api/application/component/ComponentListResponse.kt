package com.story.api.application.component

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.domain.component.Component

data class ComponentListResponse(
    val components: List<ComponentResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(components: Slice<Component, String>) = ComponentListResponse(
            components = components.data.map { component -> ComponentResponse.of(component) },
            cursor = components.cursor,
        )
    }

}
