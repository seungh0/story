package com.story.core.common.model

import com.story.core.common.error.InvalidArgumentsException

enum class CursorDirection {

    NEXT,
    PREVIOUS,
    ;

    companion object {
        private val cachedCursorDirectionMap = mutableMapOf<String, CursorDirection>()

        init {
            entries.forEach { cursorDirection -> cachedCursorDirectionMap[cursorDirection.name.lowercase()] = cursorDirection }
        }

        fun findByCode(code: String): CursorDirection {
            return cachedCursorDirectionMap[code.lowercase()]
                ?: throw InvalidArgumentsException(
                    message = "해당하는 CursorDirection($code)는 존재하지 않습니다",
                    reasons = listOf("invalid direction. available list: [${entries.joinToString(separator = ",")}]")
                )
        }
    }

}
