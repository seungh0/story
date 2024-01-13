package com.story.api.application.component

import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class ComponentListApiRequest(
    @field:Min(value = 1)
    @field:Max(value = 50)
    val pageSize: Int = 0,
) {

    fun toCursor() = CursorRequest.first(
        pageSize = pageSize,
        direction = CursorDirection.NEXT,
    )

}