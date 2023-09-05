package com.story.platform.core.common.distribution

import java.util.regex.Pattern

data class SmallDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun strategy(): DistributionStrategy {
        return TYPE
    }

    companion object {
        private val TYPE = DistributionStrategy.SMALL
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        val ALL_KEYS: MutableList<SmallDistributionKey> = mutableListOf()

        init {
            DistributionKeyGenerator.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> fromKey(key) }
        }

        fun fromKey(key: String): SmallDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return SmallDistributionKey(key)
        }

        fun makeKey(rawId: String): SmallDistributionKey {
            return fromKey(DistributionKeyGenerator.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
