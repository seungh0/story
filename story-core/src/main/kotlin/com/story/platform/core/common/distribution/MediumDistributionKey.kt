package com.story.platform.core.common.distribution

import java.util.regex.Pattern

data class MediumDistributionKey(
    override val key: String,
) : DistributionKey {

    override fun type(): DistributionKeyType {
        return TYPE
    }

    companion object {
        private val TYPE = DistributionKeyType.MEDIUM
        private val DISTRIBUTION_KEY_PATTERN = Pattern.compile(TYPE.pattern)
        private val ALL_KEYS: MutableList<MediumDistributionKey> = mutableListOf()

        init {
            DistributionKeyUtils.makeAllDistributionKeys(ALL_KEYS, TYPE.digit) { key: String -> of(key) }
        }

        fun of(key: String): MediumDistributionKey {
            require(!(key.isBlank() || !DISTRIBUTION_KEY_PATTERN.matcher(key).matches())) {
                "Not matching with DISTRIBUTION_KEY_PATTERN ( " + TYPE.pattern + " )."
            }
            return MediumDistributionKey(key)
        }

        fun fromId(rawId: String): MediumDistributionKey {
            return of(DistributionKeyUtils.hashing(TYPE.hashFormat, rawId, ALL_KEYS.size))
        }
    }

}
