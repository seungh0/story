package com.story.core.domain.subscription

import com.story.core.common.distribution.TenThousandDistributionKey

object SubscriptionDistributionKey {

    val ALL_KEYS = TenThousandDistributionKey.ALL_KEYS

    fun makeKey(subscriberId: String) = TenThousandDistributionKey.makeKey(subscriberId).key

}
