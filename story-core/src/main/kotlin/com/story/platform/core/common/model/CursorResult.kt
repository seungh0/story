package com.story.platform.core.common.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class CursorResult<E, K>(
    val data: List<E>,
    val cursor: Cursor<K>,
) {

    @JsonIgnore
    val hasNext: Boolean = cursor.hasNext

    companion object {
        fun <E, K> of(
            data: List<E>,
            cursor: Cursor<K>,
        ) = CursorResult(data = data, cursor = cursor)
    }

}
