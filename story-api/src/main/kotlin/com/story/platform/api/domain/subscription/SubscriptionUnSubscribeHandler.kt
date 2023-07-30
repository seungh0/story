package com.story.platform.api.domain.subscription

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionCountManager
import com.story.platform.core.domain.subscription.SubscriptionEventPublisher
import com.story.platform.core.domain.subscription.SubscriptionUnSubscriber
import org.springframework.stereotype.Service

@Service
class SubscriptionUnSubscribeHandler(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
    private val subscriptionCountManager: SubscriptionCountManager,
    private val subscriptionEventPublisher: SubscriptionEventPublisher,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun unsubscribe(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ) {
        componentCheckHandler.validateComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val isUnsubscribed = subscriptionUnSubscriber.unsubscribe(
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

            subscriptionEventPublisher.publishUnsubscriptionEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
