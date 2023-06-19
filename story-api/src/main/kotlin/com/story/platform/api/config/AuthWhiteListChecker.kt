package com.story.platform.api.config

import java.util.regex.Pattern

object AuthWhiteListChecker {

    private val WHITELIST_PATHS_EXACTLY = setOf<String>(
        // 필요시 추가
    )
    private val WHITELIST_PATH_PATTERN = listOf<Pattern>(
        Pattern.compile("/api/health/.*"),
    )

    fun checkNoAuthentication(path: String): Boolean {
        val isWhiteListExactly = WHITELIST_PATHS_EXACTLY.contains(path)
        if (isWhiteListExactly) {
            return true
        }
        return WHITELIST_PATH_PATTERN.any { pathPattern -> pathPattern.matcher(path).matches() }
    }

}
