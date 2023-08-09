package com.story.platform.core.domain.purge

import com.story.platform.core.common.distribution.DistributionKey
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId

data class PurgeEvent(
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
            eventKey = EventKeyGenerator.purge(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId
            ),
            payload = PurgeEvent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
            )
        )
    }

}
