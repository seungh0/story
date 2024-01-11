package com.story.core.domain.reaction

import com.story.core.domain.event.BaseEvent
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventKeyGenerator
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId

data class ReactionEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val spaceId: String,
    val accountId: String,
    val createdOptionIds: Set<String>,
    val deletedOptionIds: Set<String>,
) : BaseEvent {

    companion object {
        fun created(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            accountId: String,
            createdOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = EventKeyGenerator.reaction(
                spaceId = spaceId,
                accountId = accountId,
            ),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTIONS,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
                createdOptionIds = createdOptionIds,
                deletedOptionIds = emptySet(),
            )
        )

        fun updated(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            accountId: String,
            createdOptionIds: Set<String>,
            deletedOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            eventKey = EventKeyGenerator.reaction(
                spaceId = spaceId,
                accountId = accountId,
            ),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTIONS,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
                createdOptionIds = createdOptionIds,
                deletedOptionIds = deletedOptionIds,
            )
        )

        fun deleted(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            accountId: String,
            deletedOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.DELETED,
            eventKey = EventKeyGenerator.reaction(
                spaceId = spaceId,
                accountId = accountId,
            ),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTIONS,
                componentId = componentId,
                spaceId = spaceId,
                accountId = accountId,
                createdOptionIds = emptySet(),
                deletedOptionIds = deletedOptionIds,
            )
        )
    }

}
