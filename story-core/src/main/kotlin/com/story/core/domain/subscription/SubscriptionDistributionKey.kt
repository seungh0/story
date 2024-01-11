package com.story.core.domain.subscription

import com.story.core.common.distribution.XLargeDistributionKey

object SubscriptionDistributionKey {

    val ALL_KEYS = XLargeDistributionKey.ALL_KEYS

    fun makeKey(subscriberId: String) = XLargeDistributionKey.makeKey(subscriberId).key

}
