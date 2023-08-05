package com.story.platform.api.domain.subscription

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionCountRetriever
import com.story.platform.core.domain.subscription.SubscriptionRetriever

@HandlerAdapter
class SubscriptionRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val subscriptionRetriever: SubscriptionRetriever,
    private val subscriptionCountRetriever: SubscriptionCountRetriever,
) {

    suspend fun isSubscriber(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ): SubscriptionCheckApiResponse {
        componentCheckHandler.validateComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val isSubscriber = subscriptionRetriever.isSubscriber(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return SubscriptionCheckApiResponse(isSubscriber = isSubscriber)
    }

    suspend fun listTargetSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<SubscriberApiResponse, String> {
        componentCheckHandler.validateComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )
        val subscriptions = subscriptionRetriever.listTargetSubscribers(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            cursorRequest = cursorRequest,
        )
        return CursorResult.of(
            data = subscriptions.data.map { subscriptionReverse ->
                SubscriberApiResponse.of(subscriptionReverse)
            },
            cursor = subscriptions.cursor,
        )
    }

    suspend fun listSubscriberTargets(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<SubscriptionTargetApiResponse, String> {
        componentCheckHandler.validateComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscriptions = subscriptionRetriever.listSubscriberTargets(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            cursorRequest = cursorRequest,
        )
        return CursorResult.of(
            data = subscriptions.data.map { subscription ->
                SubscriptionTargetApiResponse.of(subscription)
            },
            cursor = subscriptions.cursor,
        )
    }

    suspend fun countSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ): SubscribersCountApiResponse {
        componentCheckHandler.validateComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscribersCount = subscriptionCountRetriever.countSubscribers(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
        )
        return SubscribersCountApiResponse(subscribersCount = subscribersCount)
    }

    suspend fun countSubscriptions(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
    ): SubscriptionsCountApiResponse {
        componentCheckHandler.validateComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscribersCount = subscriptionCountRetriever.countSubscriptions(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
        )
        return SubscriptionsCountApiResponse(subscriptionsCount = subscribersCount)
    }

}
