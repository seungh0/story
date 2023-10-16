package com.story.platform.core.common.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.story.platform.core.common.model.dto.CursorResponse

data class Slice<E, K>(
    val data: List<E>,
    val cursor: CursorResponse<K>,
) {

    @JsonIgnore
    val hasNext: Boolean = cursor.hasNext

    companion object {
        fun <E, K> of(
            data: List<E>,
            cursor: CursorResponse<K>,
        ) = Slice(data = data, cursor = cursor)
    }

}
