package com.story.core.common.distribution

import java.util.regex.Pattern

data class TenDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun strategy() = TYPE

    companion object {
        private val TYPE = DistributionStrategy.TEN
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        val ALL_KEYS: MutableList<TenDistributionKey> = mutableListOf()

        init {
            DistributionKeyGenerator.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> fromKey(key) }
        }

        fun fromKey(key: String): TenDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return TenDistributionKey(key)
        }

        fun makeKey(rawId: String): TenDistributionKey {
            return fromKey(DistributionKeyGenerator.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
