package com.story.pushcenter.core.common.partition

interface DistributionKey {

    fun type(): DistributionKeyType

    val key: String

}
