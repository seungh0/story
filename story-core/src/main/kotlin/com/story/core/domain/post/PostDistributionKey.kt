package com.story.core.domain.post

import com.story.core.common.distribution.XLargeDistributionKey

object PostDistributionKey {

    val ALL_KEYS = XLargeDistributionKey.ALL_KEYS

    fun makeKey(accountId: String) = XLargeDistributionKey.makeKey(accountId).key

}
