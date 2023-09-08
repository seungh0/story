package com.story.platform.core.common.model

import java.util.function.Function

object CursorUtils {

    fun <T, K> getCursor(listWithNextCursor: List<T>, pageSize: Int, keyGenerator: Function<T?, K?>): Cursor<K> {
        if (listWithNextCursor.size <= pageSize) {
            return Cursor.noMore()
        }

        return Cursor.of(
            cursor = keyGenerator.apply(
                listWithNextCursor.subList(0, pageSize.coerceAtMost(listWithNextCursor.size)).lastOrNull()
            )
        )
    }

}
