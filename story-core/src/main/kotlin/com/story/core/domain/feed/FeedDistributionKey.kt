package com.story.core.domain.feed

import com.story.core.common.distribution.XLargeDistributionKey

object FeedDistributionKey {

    fun makeKey(subscriberId: String) = XLargeDistributionKey.makeKey(subscriberId).key

}
