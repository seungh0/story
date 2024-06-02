package com.story.core.domain.post

import com.story.core.common.error.InvalidArgumentsException

enum class PostSortBy(
    private val description: String,
) {

    LATEST(description = "최신 순"),
    OLDEST(description = "오래된 순"),
    ;

    companion object {
        private val cachedPostSortByMap = mutableMapOf<String, PostSortBy>()

        init {
            entries
                .forEach { sortBy -> cachedPostSortByMap[sortBy.name.lowercase()] = sortBy }
        }

        fun findByCode(code: String): PostSortBy {
            return cachedPostSortByMap[code.lowercase()]
                ?: throw InvalidArgumentsException(
                    message = "해당하는 PostSortBy($code)는 존재하지 않습니다",
                    reasons = listOf(
                        "invalid sortBy. available list: [${entries.joinToString(separator = ",")}]"
                    )
                )
        }
    }

}
