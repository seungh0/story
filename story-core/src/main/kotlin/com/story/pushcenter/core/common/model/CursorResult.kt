package com.story.pushcenter.core.common.model

data class CursorResult<T>(
    val data: T,
    val cursor: CursorResponse,
) {

    companion object {
        fun <T> of(
            data: T,
            cursor: CursorResponse,
        ) = CursorResult(data = data, cursor = cursor)
    }

}
