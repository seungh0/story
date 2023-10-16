package com.story.platform.core.common.utils

import com.story.platform.core.common.model.dto.CursorResponse
import java.util.function.Function

object CursorUtils {

    fun <T, K> getCursor(listWithNextCursor: List<T>, pageSize: Int, keyGenerator: Function<T?, K?>): CursorResponse<K> {
        if (listWithNextCursor.size <= pageSize) {
            return CursorResponse.noMore()
        }

        return CursorResponse.of(
            cursor = keyGenerator.apply(
                listWithNextCursor.subList(0, pageSize.coerceAtMost(listWithNextCursor.size)).lastOrNull()
            )
        )
    }

}
