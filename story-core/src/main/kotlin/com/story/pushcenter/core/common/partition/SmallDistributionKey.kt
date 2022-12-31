package com.story.pushcenter.core.common.partition

import java.util.regex.Pattern

data class SmallDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun type(): DistributionKeyType {
        return TYPE
    }

    companion object {
        private val TYPE = DistributionKeyType.SMALL
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        private val ALL_KEYS: MutableList<SmallDistributionKey> = mutableListOf()

        init {
            DistributionKeyUtils.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> of(key) }
        }

        fun of(key: String): SmallDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return SmallDistributionKey(key)
        }

        fun fromId(rawId: String): SmallDistributionKey {
            return of(DistributionKeyUtils.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
