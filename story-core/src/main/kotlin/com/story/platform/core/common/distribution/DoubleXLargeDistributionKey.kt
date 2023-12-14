package com.story.platform.core.common.distribution

import java.util.regex.Pattern

data class DoubleXLargeDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun strategy() = TYPE

    companion object {
        private val TYPE = DistributionStrategy.DOUBLE_X_LARGE
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        val ALL_KEYS: MutableList<DoubleXLargeDistributionKey> = mutableListOf()

        init {
            DistributionKeyGenerator.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> fromKey(key) }
        }

        private fun fromKey(key: String): DoubleXLargeDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return DoubleXLargeDistributionKey(key)
        }

        fun makeKey(rawId: String): DoubleXLargeDistributionKey {
            return fromKey(DistributionKeyGenerator.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
