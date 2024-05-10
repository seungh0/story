package com.story.api.application.subscription

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.subscription.SubscribeSelfForbiddenException
import com.story.core.domain.subscription.SubscriptionCountManager
import com.story.core.domain.subscription.SubscriptionEventProducer
import com.story.core.domain.subscription.SubscriptionSubscriber

@HandlerAdapter
class SubscriptionUpsertHandler(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val subscriptionCountManager: SubscriptionCountManager,
    private val subscriptionEventProducer: SubscriptionEventProducer,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun upsertSubscription(
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

        if (targetId == subscriberId) {
            throw SubscribeSelfForbiddenException("구독자($subscriberId)가 스스로를($targetId)을 구독할 수 없습니다 [workspaceId: $workspaceId componentId: $componentId]")
        }

        val isSubscribed = subscriptionSubscriber.upsertSubscription(
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

            subscriptionEventProducer.publishCreatedEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }
    }

}
