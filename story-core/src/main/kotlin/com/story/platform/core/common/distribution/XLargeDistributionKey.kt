package com.story.platform.core.common.distribution

import java.util.regex.Pattern

data class XLargeDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun type(): DistributionKeyType {
        return TYPE
    }

    companion object {
        private val TYPE = DistributionKeyType.XLARGE
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        val ALL_KEYS: MutableList<XLargeDistributionKey> = mutableListOf()

        init {
            DistributionKeyUtils.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> fromKey(key) }
        }

        fun fromKey(key: String): XLargeDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return XLargeDistributionKey(key)
        }

        fun makeKey(rawId: String): XLargeDistributionKey {
            return fromKey(DistributionKeyUtils.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
