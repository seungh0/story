package com.story.platform.api.domain.subscription

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionCountManager
import com.story.platform.core.domain.subscription.SubscriptionEventPublisher
import com.story.platform.core.domain.subscription.SubscriptionSubscriber

@HandlerAdapter
class SubscriptionSubscribeHandler(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val subscriptionCountManager: SubscriptionCountManager,
    private val subscriptionEventPublisher: SubscriptionEventPublisher,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun subscribe(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
        alarm: Boolean,
    ) {
        componentCheckHandler.validateComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val isSubscribed = subscriptionSubscriber.subscribe(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        if (isSubscribed) {
            subscriptionCountManager.increase(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )

            subscriptionEventPublisher.publishSubscriptionEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
