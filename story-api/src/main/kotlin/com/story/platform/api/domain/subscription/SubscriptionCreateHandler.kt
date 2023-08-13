package com.story.platform.api.domain.subscription

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionCountManager
import com.story.platform.core.domain.subscription.SubscriptionCreator
import com.story.platform.core.domain.subscription.SubscriptionEventProducer

@HandlerAdapter
class SubscriptionCreateHandler(
    private val subscriptionCreator: SubscriptionCreator,
    private val subscriptionCountManager: SubscriptionCountManager,
    private val subscriptionEventProducer: SubscriptionEventProducer,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun create(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
        alarm: Boolean,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.SUBSCRIPTIONS,
            componentId = componentId,
        )

        val isSubscribed = subscriptionCreator.create(
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

            subscriptionEventProducer.publishSubscribedEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
