package com.story.platform.core.common.model

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

internal class VersionTest {

    @ValueSource(strings = ["37", "37.0", "37.0.0"])
    @ParameterizedTest
    fun `각 버전이_없는경우 0으로 인식된다`(versionString: String) {
        // when
        val version = Version.of(versionString)

        // then
        version shouldBe Version.of("37.0.0.0")
        version shouldBe Version.of("37.0.0")
        version shouldBe Version.of("37.0")
        version shouldBe Version.of("37")
    }

    @ValueSource(strings = ["", "0", "0.0", "0.0.0"])
    @ParameterizedTest
    fun `각 버전이 없는경우 0으로 인식된다 EMPTY`(versionString: String) {
        // when
        val version = Version.of(versionString)

        // then
        version shouldBe Version.of("0.0.0")
    }

    @MethodSource("isGreaterThan")
    @ParameterizedTest
    fun `큰 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val isGreaterThan = version.isGreaterThan((target))

        // then
        isGreaterThan shouldBe expectedResult
    }

    @MethodSource("isGreaterThanOrEqualTo")
    @ParameterizedTest
    fun `크거나 같은 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val isGreaterThanOrEqualTo = version.isGreaterThanOrEqualTo((target))

        // then
        isGreaterThanOrEqualTo shouldBe expectedResult
    }

    @MethodSource("is")
    @ParameterizedTest
    fun `같은 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val sut = version.`is`((target))

        // then
        sut shouldBe expectedResult
    }

    @MethodSource("isLessThan")
    @ParameterizedTest
    fun `작은 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version = Version.of("1.0.1")

        // when
        val isLessThan = version.isLessThan((target))

        // then
        isLessThan shouldBe expectedResult
    }

    @MethodSource("isLessThanOrEqualTo")
    @ParameterizedTest
    fun `작거나 같은 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val isLessThanOrEqualTo = version.isLessThanOrEqualTo((target))

        // then
        isLessThanOrEqualTo shouldBe expectedResult
    }

    companion object {
        @JvmStatic
        private fun isGreaterThan(): Stream<Arguments> = Stream.of(
            Arguments.of(Version.of("2.0.0"), false),
            Arguments.of(Version.of("1.0.2"), false),
            Arguments.of(Version.of("1.0.1.1"), false),
            Arguments.of(Version.of("1.0.1.0"), false),
            Arguments.of(Version.of("1.0.1"), false),
            Arguments.of(Version.of("1.0.0"), true),
            Arguments.of(Version.of("0.0.10"), true)
        )

        @JvmStatic
        private fun isGreaterThanOrEqualTo(): Stream<Arguments> = Stream.of(
            Arguments.of(Version.of("2.0.0"), false),
            Arguments.of(Version.of("1.0.2"), false),
            Arguments.of(Version.of("1.0.1.1"), false),
            Arguments.of(Version.of("1.0.1"), true),
            Arguments.of(Version.of("1.0.1.0"), true),
            Arguments.of(Version.of("1.0.0"), true),
            Arguments.of(Version.of("0.0.10"), true)
        )

        @JvmStatic
        private fun `is`(): Stream<Arguments> = Stream.of(
            Arguments.of(Version.of("2.0.0"), false),
            Arguments.of(Version.of("1.0.2"), false),
            Arguments.of(Version.of("1.0.1"), true),
            Arguments.of(Version.of("1.0.1.0"), true),
            Arguments.of(Version.of("1.0.0"), false),
            Arguments.of(Version.of("0.0.10"), false)
        )

        @JvmStatic
        private fun isLessThan(): Stream<Arguments> = Stream.of(
            Arguments.of(Version.of("2.0.0"), true),
            Arguments.of(Version.of("1.0.2"), true),
            Arguments.of(Version.of("1.0.1.1"), true),
            Arguments.of(Version.of("1.0.1.0"), false),
            Arguments.of(Version.of("1.0.1"), false),
            Arguments.of(Version.of("1.0.0"), false),
            Arguments.of(Version.of("0.0.10"), false)
        )

        @JvmStatic
        private fun isLessThanOrEqualTo(): Stream<Arguments> = Stream.of(
            Arguments.of(Version.of("2.0.0"), true),
            Arguments.of(Version.of("1.0.2"), true),
            Arguments.of(Version.of("1.0.1.1"), true),
            Arguments.of(Version.of("1.0.1.0"), true),
            Arguments.of(Version.of("1.0.1"), true),
            Arguments.of(Version.of("1.0.0"), false),
            Arguments.of(Version.of("0.0.10"), false)
        )
    }

}

