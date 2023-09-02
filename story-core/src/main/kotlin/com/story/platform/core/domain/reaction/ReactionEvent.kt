package com.story.platform.core.domain.reaction

import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId

data class ReactionEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val targetId: String,
    val accountId: String,
    val createdOptionIds: Set<String>,
    val deletedOptionIds: Set<String>,
) : BaseEvent {

    companion object {
        fun created(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            createdOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = EventKeyGenerator.reaction(
                targetId = targetId,
                accountId = accountId,
            ),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTION,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
                createdOptionIds = createdOptionIds,
                deletedOptionIds = emptySet(),
            )
        )

        fun updated(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            createdOptionIds: Set<String>,
            deletedOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = EventKeyGenerator.reaction(
                targetId = targetId,
                accountId = accountId,
            ),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTION,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
                createdOptionIds = createdOptionIds,
                deletedOptionIds = deletedOptionIds,
            )
        )

        fun deleted(
            workspaceId: String,
            componentId: String,
            targetId: String,
            accountId: String,
            deletedOptionIds: Set<String>,
        ) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = EventKeyGenerator.reaction(
                targetId = targetId,
                accountId = accountId,
            ),
            payload = ReactionEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.REACTION,
                componentId = componentId,
                targetId = targetId,
                accountId = accountId,
                createdOptionIds = emptySet(),
                deletedOptionIds = deletedOptionIds,
            )
        )
    }

}
