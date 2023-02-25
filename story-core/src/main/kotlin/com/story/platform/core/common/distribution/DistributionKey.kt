package com.story.platform.core.common.distribution

interface DistributionKey {

    fun type(): DistributionKeyType

    val key: String

}
