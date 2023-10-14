package com.story.platform.core.common.model.dto

import com.story.platform.core.common.model.CursorDirection

data class CursorRequest(
    val cursor: String?,
    val direction: CursorDirection,
    val pageSize: Int,
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
