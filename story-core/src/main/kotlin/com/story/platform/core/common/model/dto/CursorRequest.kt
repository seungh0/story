package com.story.platform.core.common.model.dto

import com.story.platform.core.common.model.CursorDirection
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CursorRequest(
    val cursor: String? = null,

    val direction: CursorDirection = CursorDirection.NEXT,

    @field:Min(value = 1)
    @field:Max(value = 30)
    val pageSize: Int = 0,
) {

    companion object {
        fun first(
            direction: CursorDirection,
            pageSize: Int,
        ) = CursorRequest(
            cursor = null,
            direction = direction,
            pageSize = pageSize,
        )
    }

}
