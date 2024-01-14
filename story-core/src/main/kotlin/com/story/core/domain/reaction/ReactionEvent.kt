package com.story.core.domain.reaction

import com.story.core.domain.event.BaseEvent
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId

data class ReactionEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val spaceId: String,
    val userId: String,
    val createdOptionIds: Set<String>,
    val deletedOptionIds: Set<String>,
) : BaseEvent {

    companion object {
        fun created(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            userId: String,
            createdOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = ReactionEventKey(
                spaceId = spaceId,
                userId = userId,
            ).makeKey(),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTIONS,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
                createdOptionIds = createdOptionIds,
                deletedOptionIds = emptySet(),
            )
        )

        fun updated(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            userId: String,
            createdOptionIds: Set<String>,
            deletedOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.UPDATED,
            eventKey = ReactionEventKey(
                spaceId = spaceId,
                userId = userId,
            ).makeKey(),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTIONS,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
                createdOptionIds = createdOptionIds,
                deletedOptionIds = deletedOptionIds,
            )
        )

        fun deleted(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            userId: String,
            deletedOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.DELETED,
            eventKey = ReactionEventKey(
                spaceId = spaceId,
                userId = userId,
            ).makeKey(),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTIONS,
                componentId = componentId,
                spaceId = spaceId,
                userId = userId,
                createdOptionIds = emptySet(),
                deletedOptionIds = deletedOptionIds,
            )
        )
    }

}
