package com.story.core.common.distribution

interface DistributionKey {

    fun strategy(): DistributionStrategy

    val key: String

}
