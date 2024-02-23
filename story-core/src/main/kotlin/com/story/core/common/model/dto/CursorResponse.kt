package com.story.core.common.model.dto

import java.util.Base64

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

fun CursorResponse<String>.encode(): CursorResponse<String> {
    return CursorResponse(
        hasNext = this.hasNext,
        nextCursor = this.nextCursor?.let { cursor -> Base64.getUrlEncoder().encodeToString(cursor.toByteArray()) }
    )
}
