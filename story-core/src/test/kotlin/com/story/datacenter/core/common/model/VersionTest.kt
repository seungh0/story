package com.story.datacenter.core.common.model

import org.assertj.core.api.Assertions.assertThat
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
        assertThat(version.`is`(Version.of("37.0.0.0"))).isTrue
        assertThat(version.`is`(Version.of("37.0.0"))).isTrue
        assertThat(version.`is`(Version.of("37.0"))).isTrue
        assertThat(version.`is`(Version.of("37"))).isTrue
    }

    @ValueSource(strings = ["", "0", "0.0", "0.0.0"])
    @ParameterizedTest
    fun `각 버전이 없는경우 0으로 인식된다 EMPTY`(versionString: String) {
        // when
        val version = Version.of(versionString)

        // then
        assertThat(version.`is`(Version.of("0.0.0"))).isTrue
    }

    @MethodSource("isGreaterThan")
    @ParameterizedTest
    fun `큰 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val isGreaterThan = version.isGreaterThan((target))

        // then
        assertThat(isGreaterThan).isEqualTo(expectedResult)
    }

    @MethodSource("isGreaterThanOrEqualTo")
    @ParameterizedTest
    fun `크거나 같은 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val isGreaterThanOrEqualTo = version.isGreaterThanOrEqualTo((target))

        // then
        assertThat(isGreaterThanOrEqualTo).isEqualTo(expectedResult)
    }

    @MethodSource("is")
    @ParameterizedTest
    fun `같은 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val `is` = version.`is`((target))

        // then
        assertThat(`is`).isEqualTo(expectedResult)
    }

    @MethodSource("isLessThan")
    @ParameterizedTest
    fun `작은 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val isLessThan = version.isLessThan((target))

        // then
        assertThat(isLessThan).isEqualTo(expectedResult)
    }

    @MethodSource("isLessThanOrEqualTo")
    @ParameterizedTest
    fun `작거나 같은 버전인지 체크한다`(target: Version, expectedResult: Boolean) {
        // given
        val version: Version = Version.of("1.0.1")

        // when
        val isLessThanOrEqualTo = version.isLessThanOrEqualTo((target))

        // then
        assertThat(isLessThanOrEqualTo).isEqualTo(expectedResult)
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

