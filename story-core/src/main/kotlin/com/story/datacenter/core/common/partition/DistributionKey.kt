package com.story.datacenter.core.common.partition

interface DistributionKey {

    fun type(): DistributionKeyType

    val key: String

}
