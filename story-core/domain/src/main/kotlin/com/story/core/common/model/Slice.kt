package com.story.core.common.model

import com.story.core.common.model.dto.CursorResponse

data class Slice<E, K>(
    val data: List<E>,
    val cursor: CursorResponse<K>,
) {

    fun hasNext(): Boolean = cursor.hasNext

    companion object {
        fun <E, K> noMore(
            data: List<E>,
        ) = Slice(data = data, cursor = CursorResponse.noMore<K>())

        fun <E, K> of(
            data: List<E>,
            cursor: CursorResponse<K>,
        ) = Slice(data = data, cursor = cursor)
    }

}
