package com.story.platform.api.config

import com.story.platform.api.config.auth.AuthenticationWhitelistChecker
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import org.springframework.util.StopWatch

internal class AuthWhiteListCheckerTest : FunSpec({

    test("WhiteList Path Regex Pattern") {
        forAll(
            table(
                headers("uri", "expected"),
                row("/api/health/readiness", true),
                row("/api/health/liveness", true),
                row("/api/test", false),
            )
        ) { uri, expected ->
            // when
            val sut = AuthenticationWhitelistChecker.checkNoAuthentication(uri)

            // then
            sut shouldBe expected
        }
    }

    test("BenchMarker") {
        forAll(
            table(
                headers("uri"),
                row("/api/health/readiness"),
                row("/api/health/liveness"),
                row("/api/test")
            )
        ) { uri ->
            // given
            val stopWatcher = StopWatch()
            stopWatcher.start()

            // when
            AuthenticationWhitelistChecker.checkNoAuthentication(uri)

            // then
            stopWatcher.stop()
            stopWatcher.totalTimeMillis shouldBeLessThanOrEqual 1
        }
    }

})
