package com.story.platform.core.common.partition

interface DistributionKey {

    fun type(): DistributionKeyType

    val key: String

}
