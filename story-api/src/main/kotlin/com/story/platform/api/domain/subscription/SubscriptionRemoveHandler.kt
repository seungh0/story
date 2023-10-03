package com.story.platform.api.domain.subscription

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionCountManager
import com.story.platform.core.domain.subscription.SubscriptionEventProducer
import com.story.platform.core.domain.subscription.SubscriptionUnSubscriber

@HandlerAdapter
class SubscriptionRemoveHandler(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
    private val subscriptionCountManager: SubscriptionCountManager,
    private val subscriptionEventProducer: SubscriptionEventProducer,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun removeSubscription(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val isUnsubscribed = subscriptionUnSubscriber.removeSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        if (isUnsubscribed) {
            subscriptionCountManager.decrease(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            subscriptionEventProducer.publishUnsubscribedEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
