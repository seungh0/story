package com.story.core.common.distribution

import java.util.regex.Pattern

data class HundredDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun strategy() = TYPE

    companion object {
        private val TYPE = DistributionStrategy.HUNDRED
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        val ALL_KEYS: MutableList<HundredDistributionKey> = mutableListOf()

        init {
            DistributionKeyGenerator.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> fromKey(key) }
        }

        fun fromKey(key: String): HundredDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return HundredDistributionKey(key)
        }

        fun makeKey(rawId: String): HundredDistributionKey {
            return fromKey(DistributionKeyGenerator.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
