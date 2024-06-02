package com.story.core.domain.purge

import com.story.core.common.distribution.DistributionKey
import com.story.core.domain.resource.ResourceId

interface Purger {

    fun targetResourceId(): ResourceId

    fun distributeKeys(): Collection<DistributionKey>

    suspend fun clear(workspaceId: String, componentId: String, distributionKey: DistributionKey): Long

}
