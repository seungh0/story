package com.story.platform.core.common.distribution

interface DistributionKey {

    fun strategy(): DistributionStrategy

    val key: String

}
