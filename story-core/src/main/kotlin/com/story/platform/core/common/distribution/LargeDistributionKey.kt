package com.story.platform.core.common.distribution

import java.util.regex.Pattern

data class LargeDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun type(): DistributionKeyType {
        return TYPE
    }

    companion object {
        private val TYPE = DistributionKeyType.LARGE
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        val ALL_KEYS: MutableList<LargeDistributionKey> = mutableListOf()

        init {
            DistributionKeyUtils.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> of(key) }
        }

        fun of(key: String): LargeDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return LargeDistributionKey(key)
        }

        fun fromId(rawId: String): LargeDistributionKey {
            return of(DistributionKeyUtils.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
