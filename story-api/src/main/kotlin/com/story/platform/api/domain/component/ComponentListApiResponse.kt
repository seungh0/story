package com.story.platform.api.domain.component

import com.story.platform.core.common.model.ContentsWithCursor
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.domain.component.ComponentResponse

data class ComponentListApiResponse(
    val components: List<ComponentApiResponse>,
    val cursor: Cursor<String>,
) {

    companion object {
        fun of(components: ContentsWithCursor<ComponentResponse, String>) = ComponentListApiResponse(
            components = components.data.map { component -> ComponentApiResponse.of(component) },
            cursor = components.cursor,
        )
    }

}
