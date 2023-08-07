package com.story.platform.core.common.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

internal class VersionTest : FunSpec({

    test("각 버전이 없는경우 0으로 인식된다") {
        forAll(
            table(
                headers("versionString"),
                row("37"),
                row("37.0"),
                row("37.0,0"),
            )
        ) { versionString ->
            // when
            val version = Version.of(versionString)

            // then
            version shouldBe Version.of("37.0.0.0")
            version shouldBe Version.of("37.0.0")
            version shouldBe Version.of("37.0")
            version shouldBe Version.of("37")
        }
    }

    test("각 버전이 없는경우 0으로 인식된다 EMPTY") {
        forAll(
            table(
                headers("versionString"),
                row(""),
                row("0"),
                row("0.0"),
                row("0.0.0"),
            )
        ) { versionString ->
            // when
            val version = Version.of(versionString)

            // then
            version shouldBe Version.of("0.0.0")
        }
    }

    test("큰 버전인지 체크한다") {
        forAll(
            table(
                headers("target", "expectedResult"),
                row(Version.of("2.0.0"), false),
                row(Version.of("1.0,2"), false),
                row(Version.of("1.0.1.1"), false),
                row(Version.of("1.0.1.0"), false),
                row(Version.of("1.0.1"), false),
                row(Version.of("1.0.0"), true),
                row(Version.of("0.0.10"), true),
            )
        ) { target, expectedResult ->
            // given
            val version: Version = Version.of("1.0.1")

            // when
            val isGreaterThan = version > target

            // then
            isGreaterThan shouldBe expectedResult
        }
    }

    test("크거나 같은 버전인지 체크한다") {
        forAll(
            table(
                headers("target", "expectedResult"),
                row(Version.of("2.0.0"), false),
                row(Version.of("1.0.2"), false),
                row(Version.of("1.0.1.1"), false),
                row(Version.of("1.0.1"), true),
                row(Version.of("1.0.1.0"), true),
                row(Version.of("1.0.0"), true),
                row(Version.of("0.0.10"), true)
            )
        ) { target, expectedResult ->
            // given
            val version: Version = Version.of("1.0.1")

            // when
            val isGreaterThanOrEqualTo = version >= target

            // then
            isGreaterThanOrEqualTo shouldBe expectedResult
        }
    }

    test("같은 버전인지 체크한다") {
        forAll(
            table(
                headers("target", "expectedResult"),
                row(Version.of("2.0.0"), false),
                row(Version.of("1.0.2"), false),
                row(Version.of("1.0.1"), true),
                row(Version.of("1.0.1.0"), true),
                row(Version.of("1.0.0"), false),
                row(Version.of("0.0.10"), false),
            )
        ) { target, expectedResult ->
            // given
            val version: Version = Version.of("1.0.1")

            // when
            val isGreaterThanOrEqualTo = version == target

            // then
            isGreaterThanOrEqualTo shouldBe expectedResult
        }
    }

    test("작은 버전인지 체크한다") {
        forAll(
            table(
                headers("target", "expectedResult"),
                row(Version.of("2.0.0"), true),
                row(Version.of("1.0.2"), true),
                row(Version.of("1.0.1.1"), true),
                row(Version.of("1.0.1.0"), false),
                row(Version.of("1.0.1"), false),
                row(Version.of("1.0.0"), false),
            )
        ) { target, expectedResult ->
            // given
            val version = Version.of("1.0.1")

            // when
            val isLessThan = version < target

            // then
            isLessThan shouldBe expectedResult
        }
    }

    test("작거나 같은 버전인지 체크한다") {
        forAll(
            table(
                headers("target", "expectedResult"),
                row(Version.of("2.0.0"), true),
                row(Version.of("1.0.2"), true),
                row(Version.of("1.0.1.1"), true),
                row(Version.of("1.0.1.0"), true),
                row(Version.of("1.0.1"), true),
                row(Version.of("1.0.0"), false),
            )
        ) { target, expectedResult ->
            // given
            val version = Version.of("1.0.1")

            // when
            val isLessThan = version <= target

            // then
            isLessThan shouldBe expectedResult
        }
    }

})
