package com.story.platform.core.common.model.dto

data class CursorResponse<T>(
    val nextCursor: T?,
    val hasNext: Boolean,
) {

    companion object {
        fun <T> of(
            cursor: T?,
        ) = CursorResponse(
            nextCursor = cursor,
            hasNext = cursor != null,
        )

        fun <T> noMore(): CursorResponse<T> = of(null)
    }

}
