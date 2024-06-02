package com.story.core.common.distribution

import java.util.regex.Pattern

data class ThousandDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun strategy() = TYPE

    companion object {
        private val TYPE = DistributionStrategy.THOUSAND
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        val ALL_KEYS: MutableList<ThousandDistributionKey> = mutableListOf()

        init {
            DistributionKeyGenerator.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> fromKey(key) }
        }

        fun fromKey(key: String): ThousandDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return ThousandDistributionKey(key)
        }

        fun makeKey(rawId: String): ThousandDistributionKey {
            return fromKey(DistributionKeyGenerator.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
