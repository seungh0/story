package com.story.platform.core.domain.feed

import com.story.platform.core.common.distribution.XLargeDistributionKey

object FeedDistributionKey {

    fun makeKey(subscriberId: String) = XLargeDistributionKey.makeKey(subscriberId).key

}
