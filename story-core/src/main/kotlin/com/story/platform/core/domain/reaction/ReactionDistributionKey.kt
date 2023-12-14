package com.story.platform.core.domain.reaction

import com.story.platform.core.common.distribution.XLargeDistributionKey

object ReactionDistributionKey {

    val ALL_KEYS = XLargeDistributionKey.ALL_KEYS

    fun makeKey(accountId: String) = XLargeDistributionKey.makeKey(accountId).key

}
