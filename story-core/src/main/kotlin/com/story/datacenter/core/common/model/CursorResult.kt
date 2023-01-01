package com.story.datacenter.core.common.model

data class CursorResult<T>(
    val data: List<T>,
    val cursor: CursorResponse,
) {

    companion object {
        fun <T> of(
            data: List<T>,
            cursor: CursorResponse,
        ) = CursorResult(data = data, cursor = cursor)
    }

}
