package com.story.api.application.feed

import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class FeedListApiRequest(
    val cursor: String? = null,

    val direction: String = CursorDirection.NEXT.name,

    @field:Min(value = 1)
    @field:Max(value = 50)
    val pageSize: Int = 0,
) {

    fun toCursor() = CursorRequest(
        cursor = cursor,
        direction = CursorDirection.findByCode(direction),
        pageSize = pageSize,
    )

}
