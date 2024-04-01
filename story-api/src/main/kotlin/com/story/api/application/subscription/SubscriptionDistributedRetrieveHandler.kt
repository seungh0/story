package com.story.api.application.subscription

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.common.distribution.SlotRangeMarker
import com.story.core.common.model.dto.SlotRangeMarkerResponse
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.subscription.SubscriptionDistributedRetriever
import com.story.core.domain.subscription.SubscriptionResponse

@HandlerAdapter
class SubscriptionDistributedRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val subscriptionDistributedRetriever: SubscriptionDistributedRetriever,
) {

    suspend fun getSubscriberDistributedMarkers(
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
            markerSize = request.markerSize,
        )
        return SubscriberDistributedMarkerListResponse(
            markers = markers.map { marker -> marker.makeCursor() },
        )
    }

    suspend fun listSubscribersByDistributedMarkers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        request: SubscriberListApiRequest,
    ): SlotRangeMarkerResponse<List<SubscriptionResponse>> {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        return subscriptionDistributedRetriever.listSubscribersByDistributedmarkers(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            marker = SlotRangeMarker.fromCursor(request.cursor),
            pageSize = request.pageSize,
        )
    }

}
