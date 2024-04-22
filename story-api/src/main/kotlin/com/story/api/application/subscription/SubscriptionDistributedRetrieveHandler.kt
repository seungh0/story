package com.story.api.application.subscription

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.distribution.SlotRangeMarker
import com.story.core.common.error.InvalidArgumentsException
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.subscription.SubscriptionDistributedRetriever

@HandlerAdapter
class SubscriptionDistributedRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val subscriptionDistributedRetriever: SubscriptionDistributedRetriever,
) {

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
        request: SubscriberListApiRequest,
    ): SubscriberListApiResponse {
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

        return SubscriberListApiResponse.of(subscribers = subscribers)
    }

}
