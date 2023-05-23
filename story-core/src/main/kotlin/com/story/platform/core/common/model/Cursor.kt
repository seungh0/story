package com.story.platform.core.common.model

data class Cursor<T>(
    val cursor: T?,
    val hasNext: Boolean,
) {

    companion object {
        fun <T> of(
            cursor: T?,
        ) = Cursor(
            cursor = cursor,
            hasNext = cursor != null,
        )
    }

}
