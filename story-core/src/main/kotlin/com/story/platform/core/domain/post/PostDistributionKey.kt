package com.story.platform.core.domain.post

import com.story.platform.core.common.distribution.XLargeDistributionKey

object PostDistributionKey {

    val ALL_KEYS = XLargeDistributionKey.ALL_KEYS

    fun makeKey(accountId: String) = XLargeDistributionKey.makeKey(accountId).key

}
