package com.story.platform.core.domain.purge

import com.story.platform.core.common.distribution.DistributionKey
import com.story.platform.core.domain.resource.ResourceId

interface Purger {

    fun targetResourceId(): ResourceId

    fun distributeKeys(): Collection<DistributionKey>

    suspend fun clear(workspaceId: String, componentId: String, distributionKey: DistributionKey): Long

}
