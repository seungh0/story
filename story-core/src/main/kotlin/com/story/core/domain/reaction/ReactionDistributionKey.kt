package com.story.core.domain.reaction

import com.story.core.common.distribution.XLargeDistributionKey

object ReactionDistributionKey {

    val ALL_KEYS = XLargeDistributionKey.ALL_KEYS

    fun makeKey(accountId: String) = XLargeDistributionKey.makeKey(accountId).key

}
