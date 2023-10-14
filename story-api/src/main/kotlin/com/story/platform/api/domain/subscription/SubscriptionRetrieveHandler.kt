package com.story.platform.api.domain.subscription

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionCountRetriever
import com.story.platform.core.domain.subscription.SubscriptionRetriever

@HandlerAdapter
class SubscriptionRetrieveHandler(
    private val componentCheckHandler: ComponentCheckHandler,
    private val subscriptionRetriever: SubscriptionRetriever,
    private val subscriptionCountRetriever: SubscriptionCountRetriever,
) {

    suspend fun existsSubscription(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ): SubscriptionExistsApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val isSubscriber = subscriptionRetriever.existsSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return SubscriptionExistsApiResponse(isSubscriber = isSubscriber)
    }

    suspend fun listSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): SubscriberListApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )
        val subscriptions = subscriptionRetriever.listSubscribers(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            cursorRequest = cursorRequest,
        )

        return SubscriberListApiResponse.of(subscriptions = subscriptions)
    }

    suspend fun listSubscriptionTargets(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): SubscriptionTargetListApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val subscriptions = subscriptionRetriever.listSubscriptionTargets(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            cursorRequest = cursorRequest,
        )

        return SubscriptionTargetListApiResponse.of(subscriptions = subscriptions)
    }

    suspend fun countSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ): SubscriberCountApiResponse {
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
        return SubscriberCountApiResponse(subscriberCount = subscriberCount)
    }

    suspend fun countTargets(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
    ): SubscriptionTargetCountApiResponse {
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
        return SubscriptionTargetCountApiResponse(targetCount = subscriberCount)
    }

}
