package com.story.platform.core.common.model

data class CursorResult<E, K>(
    val data: List<E>,
    val cursor: com.story.platform.core.common.model.Cursor<K>,
) {

    companion object {
        fun <E, K> of(
            data: List<E>,
            cursor: com.story.platform.core.common.model.Cursor<K>,
        ) = com.story.platform.core.common.model.CursorResult(data = data, cursor = cursor)
    }

}
