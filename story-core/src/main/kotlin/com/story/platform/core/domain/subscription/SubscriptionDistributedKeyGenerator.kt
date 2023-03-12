package com.story.platform.core.domain.subscription

import com.story.platform.core.common.distribution.LargeDistributionKey

object SubscriptionDistributedKeyGenerator {

    fun generate(subscriberId: String): String {
        return LargeDistributionKey.fromId(subscriberId).key
    }

    val KEYS = LargeDistributionKey.ALL_KEYS

}
