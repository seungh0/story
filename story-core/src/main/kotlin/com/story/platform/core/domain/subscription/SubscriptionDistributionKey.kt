package com.story.platform.core.domain.subscription

import com.story.platform.core.common.distribution.XLargeDistributionKey

object SubscriptionDistributionKey {

    val ALL_KEYS = XLargeDistributionKey.ALL_KEYS

    fun makeKey(subscriberId: String) = XLargeDistributionKey.makeKey(subscriberId).key

}
