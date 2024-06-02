package com.story.api.application.feed.mapping

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.component.ComponentNotExistsException
import com.story.core.domain.component.ComponentReaderWithCache
import com.story.core.domain.feed.mapping.FeedMappingCreateCommand
import com.story.core.domain.feed.mapping.FeedMappingCreator
import com.story.core.domain.feed.mapping.FeedMappingEventProducer
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedMappingCreateHandler(
    private val componentReaderWithCache: ComponentReaderWithCache,
    private val feedMappingCreator: FeedMappingCreator,
    private val feedMappingEventProducer: FeedMappingEventProducer,
) {

    suspend fun create(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
        request: FeedMappingCreateRequest,
    ) {
        setOf(
            ResourceId.FEEDS to feedComponentId,
            sourceResourceId to sourceComponentId,
            ResourceId.SUBSCRIPTIONS to subscriptionComponentId,
        ).forEach { (resourceId: ResourceId, componentId: String) ->
            componentReaderWithCache.getComponent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
                .orElseThrow { ComponentNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 컴포넌트($resourceId-$componentId)입니다") }
        }

        feedMappingCreator.create(
            command = FeedMappingCreateCommand(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                description = request.description,
                retention = request.retention,
                subscriptionComponentId = subscriptionComponentId,
            )
        )

        feedMappingEventProducer.publishCreatedEvent(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceComponentId = sourceComponentId,
            sourceResourceId = sourceResourceId,
            subscriptionComponentId = subscriptionComponentId,
        )
    }

}
