package com.story.api.application.subscription

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.distribution.SlotRangeMarker
import com.story.core.common.error.InvalidArgumentsException
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.subscription.SubscriptionCountRetriever
import com.story.core.domain.subscription.SubscriptionDistributedRetriever
import com.story.core.domain.subscription.SubscriptionReader

@HandlerAdapter
class SubscriptionRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val subscriptionReader: SubscriptionReader,
    private val subscriptionDistributedRetriever: SubscriptionDistributedRetriever,
    private val subscriptionCountRetriever: SubscriptionCountRetriever,
) {

    suspend fun existsSubscription(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ): SubscriptionExistsResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val isSubscriber = subscriptionReader.existsSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return SubscriptionExistsResponse(isSubscriber = isSubscriber)
    }

    suspend fun listSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        request: SubscriberListRequest,
    ): SubscriberListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )
        val subscriptions = subscriptionReader.listSubscribers(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            cursorRequest = request.toDecodedCursor(),
        )

        return SubscriberListResponse.of(subscribers = subscriptions)
    }

    suspend fun listSubscriberDistributedMarkers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        request: SubscriberDistributedMarkerListRequest,
    ): SubscriberDistributedMarkerListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val markers = subscriptionDistributedRetriever.getSubscriberDistributedMarkers(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            markerSize = request.parallelSize,
        )
        return SubscriberDistributedMarkerListResponse(
            cursors = markers.map { marker -> marker.makeCursor() },
        )
    }

    suspend fun listSubscribersByDistributedMarkers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        request: SubscriberListRequest,
    ): SubscriberListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscribers = subscriptionDistributedRetriever.listSubscribersByDistributedmarkers(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            marker = SlotRangeMarker.fromCursor(
                request.cursor ?: throw InvalidArgumentsException(
                    message = "cursor is null",
                    reasons = listOf("cursor is null")
                )
            ),
            pageSize = request.pageSize,
        )

        return SubscriberListResponse.of(subscribers = subscribers)
    }

    suspend fun listSubscriptionTargets(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        request: SubscriptionTargetListRequest,
    ): SubscriptionTargetListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscriptions = subscriptionReader.listSubscriptionTargets(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            cursorRequest = request.toDecodedCursor(),
        )

        return SubscriptionTargetListResponse.of(subscriptions = subscriptions)
    }

    suspend fun countSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ): SubscriberCountResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscriberCount = subscriptionCountRetriever.countSubscribers(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
        return SubscriberCountResponse(subscriberCount = subscriberCount)
    }

    suspend fun countTargets(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
    ): SubscriptionTargetCountResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscriberCount = subscriptionCountRetriever.countTargets(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return SubscriptionTargetCountResponse(targetCount = subscriberCount)
    }

}
