package com.story.platform.core.domain.purge

import com.story.platform.core.common.distribution.DistributionKey
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class PurgeManager(
    private val purgerFinder: PurgerFinder,
    private val purgeDistributeEventProducer: PurgeDistributeEventProducer,
) {

    suspend fun publishEvent(resourceId: ResourceId, workspaceId: String, componentId: String) {
        purgeDistributeEventProducer.publishEvents(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            distributionKeys = purgerFinder[resourceId].distributeKeys()
        )
    }

    suspend fun clear(
        resourceId: ResourceId,
        workspaceId: String,
        componentId: String,
        distributionKey: DistributionKey,
    ): Long {
        return purgerFinder[resourceId].clear(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = distributionKey
        )
    }

}
