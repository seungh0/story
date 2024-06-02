package com.story.core.common.distribution

import java.util.regex.Pattern

data class HunredThousandDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun strategy() = TYPE

    companion object {
        private val TYPE = DistributionStrategy.HUNDRED_THOUSAND
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        val ALL_KEYS: MutableList<HunredThousandDistributionKey> = mutableListOf()

        init {
            DistributionKeyGenerator.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> fromKey(key) }
        }

        private fun fromKey(key: String): HunredThousandDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return HunredThousandDistributionKey(key)
        }

        fun makeKey(rawId: String): HunredThousandDistributionKey {
            return fromKey(DistributionKeyGenerator.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
