package com.story.api.config.apikey

import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.util.PathMatcher

@Component
class ApiKeyChecker(
    private val pathMatcher: PathMatcher = AntPathMatcher(),
) {

    fun shouldCheckApiKey(method: HttpMethod, path: String): Boolean {
        return !skipCheckApiKey(method, path)
    }

    private fun skipCheckApiKey(method: HttpMethod, path: String): Boolean {
        return skipApiKeyCheckRequestPatterns.any { (httpMethod, pathPattern) ->
            httpMethod == method && pathMatcher.match(pathPattern, path)
        }
    }

    private val skipApiKeyCheckRequestPatterns = listOf(
        GET to "/api/health/**",
        GET to "/monitoring/**",
        GET to "/api/v1/api-keys/**",
        POST to "/api/setup",
        DELETE to "/api/test/caches-refresh",
    )

}
