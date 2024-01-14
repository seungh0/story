package com.story.core.domain.purge

import com.story.core.common.distribution.DistributionKey
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId

data class PurgeDistributeEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val distributionKey: String,
) {

    companion object {
        fun created(
            workspaceId: String,
            resourceId: ResourceId,
            componentId: String,
            distributionKey: DistributionKey,
        ) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = PurgeEventKey(
                resourceId = resourceId,
                componentId = componentId
            ).makeKey(),
            payload = PurgeDistributeEvent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
            )
        )
    }

}
