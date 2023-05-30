package com.story.platform.core.common.model

data class Cursor<T>(
    val nextCursor: T?,
    val hasNext: Boolean,
) {

    companion object {
        fun <T> of(
            cursor: T?,
        ) = Cursor(
            nextCursor = cursor,
            hasNext = cursor != null,
        )
    }

}
