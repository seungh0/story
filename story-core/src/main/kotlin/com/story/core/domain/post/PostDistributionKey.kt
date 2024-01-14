package com.story.core.domain.post

import com.story.core.common.distribution.TenThousandDistributionKey

object PostDistributionKey {

    val ALL_KEYS = TenThousandDistributionKey.ALL_KEYS

    fun makeKey(ownerId: String) = TenThousandDistributionKey.makeKey(ownerId).key

}
