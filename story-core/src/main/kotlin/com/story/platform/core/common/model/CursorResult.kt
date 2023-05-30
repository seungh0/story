package com.story.platform.core.common.model

data class CursorResult<E, K>(
    val data: List<E>,
    val cursor: Cursor<K>,
) {

    fun hasNext() = cursor.nextCursor == null

    companion object {
        fun <E, K> of(
            data: List<E>,
            cursor: Cursor<K>,
        ) = CursorResult(data = data, cursor = cursor)
    }

}
