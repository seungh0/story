package com.story.api.config.apikey

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpMethod
import org.springframework.util.AntPathMatcher

internal class ApiKeyCheckerTest : StringSpec({

    val apiKeyWhitelistChecker = ApiKeyChecker(
        pathMatcher = AntPathMatcher()
    )

    "인증 체크를 수행하지 않는 WhiteList Path에 대한 정규식 패턴을 검증한다" {
        forAll(
            table(
                headers("method", "path", "expected"),
                row(HttpMethod.GET, "/api/health/readiness", false),
                row(HttpMethod.GET, "/api/health/liveness", false),
                row(HttpMethod.PATCH, "/api/health/readiness", true),
                row(HttpMethod.GET, "/api/test", true),
            )
        ) { method, path, expected ->
            // when
            val sut = apiKeyWhitelistChecker.shouldCheckApiKey(method = method, path = path)

            // then
            sut shouldBe expected
        }
    }

})
