package com.story.api.config.auth

import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import java.util.regex.Pattern

object AuthenticationWhitelistChecker {

    private val WHITELIST_PATHS_EXACTLY = setOf<String>(
        // 필요시 추가
    )
    private val WHITELIST_PATH_PATTERN = listOf<Pair<HttpMethod, Pattern>>(
        GET to Pattern.compile("/api/health/.*"),
        GET to Pattern.compile("/monitoring/.*"),
        GET to Pattern.compile("/api/v1/authentications/api-key"),
        POST to Pattern.compile("/api/setup")
    )

    fun checkNoAuthentication(method: HttpMethod, path: String): Boolean {
        val isWhiteListExactly = WHITELIST_PATHS_EXACTLY.contains(path)
        if (isWhiteListExactly) {
            return true
        }
        return WHITELIST_PATH_PATTERN.any { (httpMethod, pathPattern) ->
            httpMethod == method && pathPattern.matcher(path).matches()
        }
    }

}
