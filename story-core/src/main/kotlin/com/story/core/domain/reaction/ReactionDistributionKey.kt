package com.story.core.domain.reaction

import com.story.core.common.distribution.TenThousandDistributionKey

object ReactionDistributionKey {

    val ALL_KEYS = TenThousandDistributionKey.ALL_KEYS

    fun makeKey(userId: String) = TenThousandDistributionKey.makeKey(userId).key

}
