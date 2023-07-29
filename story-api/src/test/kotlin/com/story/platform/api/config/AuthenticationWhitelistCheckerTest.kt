package com.story.platform.api.config

import com.story.platform.api.config.auth.AuthenticationWhitelistChecker
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpMethod
import org.springframework.util.StopWatch

internal class AuthenticationWhitelistCheckerTest : FunSpec({

    test("WhiteList Path Regex Pattern") {
        forAll(
            table(
                headers("method", "path", "expected"),
                row(HttpMethod.GET, "/api/health/readiness", true),
                row(HttpMethod.GET, "/api/health/liveness", true),
                row(HttpMethod.PATCH, "/api/health/readiness", false),
                row(HttpMethod.GET, "/api/test", false),
            )
        ) { method, path, expected ->
            // when
            val sut = AuthenticationWhitelistChecker.checkNoAuthentication(method = method, path = path)

            // then
            sut shouldBe expected
        }
    }

    test("BenchMark") {
        forAll(
            table(
                headers("method", "path"),
                row(HttpMethod.GET, "/api/health/readiness"),
                row(HttpMethod.GET, "/api/health/liveness"),
                row(HttpMethod.GET, "/api/test")
            )
        ) { method, path ->
            // given
            val stopWatcher = StopWatch()
            stopWatcher.start()

            // when
            AuthenticationWhitelistChecker.checkNoAuthentication(method = method, path = path)

            // then
            stopWatcher.stop()
            stopWatcher.totalTimeMillis shouldBeLessThanOrEqual 1
        }
    }

})
