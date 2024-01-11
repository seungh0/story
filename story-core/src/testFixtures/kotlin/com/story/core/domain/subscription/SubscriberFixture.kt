package com.story.core.domain.subscription

import com.story.core.support.RandomGenerator.generateBoolean
import com.story.core.support.RandomGenerator.generateLong
import com.story.core.support.RandomGenerator.generateString

object SubscriberFixture {

    fun create(
        workspaceId: String = generateString(),
        componentId: String = generateString(),
        targetId: String = generateString(),
        slotId: Long = generateLong(),
        subscriberId: String = generateString(),
        alarmEnabled: Boolean = generateBoolean(),
    ) = Subscriber(
        key = SubscriberPrimaryKey(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
        ),
        alarmEnabled = alarmEnabled,
    )

    fun create(
        subscription: Subscription,
    ) = Subscriber(
        key = SubscriberPrimaryKey(
            workspaceId = subscription.key.subscriberId,
            componentId = subscription.key.componentId,
            targetId = subscription.key.targetId,
            slotId = subscription.slotId,
            subscriberId = subscription.key.subscriberId,
        ),
        alarmEnabled = subscription.alarmEnabled,
    )

}
