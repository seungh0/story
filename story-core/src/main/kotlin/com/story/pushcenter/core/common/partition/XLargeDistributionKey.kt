package com.story.pushcenter.core.common.partition

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
        private val ALL_KEYS: MutableList<XLargeDistributionKey> = mutableListOf()

        init {
            DistributionKeyUtils.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> of(key) }
        }

        fun of(key: String): XLargeDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return XLargeDistributionKey(key)
        }

        fun fromId(rawId: String): XLargeDistributionKey {
            return of(DistributionKeyUtils.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
