package com.story.platform.core.common.model

import java.io.Serializable
import java.util.regex.Pattern
import java.util.stream.IntStream

data class Version(
    val version: String,
    override val length: Int,
) : Comparable<com.story.platform.core.common.model.Version>, CharSequence, Serializable {

    fun isGreaterThan(version: com.story.platform.core.common.model.Version): Boolean {
        return compareTo(version) > 0
    }

    fun isGreaterThanOrEqualTo(version: com.story.platform.core.common.model.Version): Boolean {
        return compareTo(version) >= 0
    }

    fun `is`(version: com.story.platform.core.common.model.Version): Boolean {
        return equals(version)
    }

    fun isLessThan(version: com.story.platform.core.common.model.Version): Boolean {
        return compareTo(version) < 0
    }

    fun isLessThanOrEqualTo(version: com.story.platform.core.common.model.Version): Boolean {
        return compareTo(version) <= 0
    }

    override fun hashCode(): Int {
        return version.hashCode()
    }

    override fun compareTo(other: com.story.platform.core.common.model.Version): Int {
        val versionArray: Array<String> =
            version.split(com.story.platform.core.common.model.Version.Companion.VERSION_REGEX.pattern().toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val targetVersionArray: Array<String> = other.toString()
            .split(com.story.platform.core.common.model.Version.Companion.VERSION_REGEX.pattern().toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()


        for (i in 0..versionArray.size.coerceAtLeast(targetVersionArray.size)) {
            val version: Int = getVersion(versionArray, i)
            val targetVersion: Int = getVersion(targetVersionArray, i)
            if (version != targetVersion) {
                return version - targetVersion
            }
        }

        return 0
    }

    private fun getVersion(versionArray: Array<String>, index: Int): Int {
        return try {
            versionArray[index].toInt()
        } catch (e: Exception) {
            0
        }
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return version.subSequence(startIndex, endIndex)
    }

    override fun toString(): String {
        return version
    }

    override fun chars(): IntStream {
        return version.chars()
    }

    override fun codePoints(): IntStream {
        return version.codePoints()
    }

    override fun get(index: Int): Char {
        return version[index]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is com.story.platform.core.common.model.Version) {
            return false
        }
        return compareTo(other) == 0
    }

    companion object {
        private val EMPTY = com.story.platform.core.common.model.Version("", 0)

        private val VERSION_REGEX = Pattern.compile("[^a-zA-Z\\d]+")

        fun of(version: String?): com.story.platform.core.common.model.Version {
            if (version.isNullOrBlank()) {
                return com.story.platform.core.common.model.Version.Companion.EMPTY
            }
            return com.story.platform.core.common.model.Version(
                version = version,
                length = version.length,
            )
        }
    }

}
