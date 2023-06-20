package com.story.platform.core.domain.subscription

import com.story.platform.core.support.RandomGenerator.generateBoolean
import com.story.platform.core.support.RandomGenerator.generateEnum
import com.story.platform.core.support.RandomGenerator.generateLong
import com.story.platform.core.support.RandomGenerator.generateString

object SubscriberFixture {

    fun create(
        workspaceId: String = generateString(),
        subscriptionType: SubscriptionType = generateEnum(SubscriptionType::class.java),
        targetId: String = generateString(),
        slotId: Long = generateLong(),
        subscriberId: String = generateString(),
        alarm: Boolean = generateBoolean(),
    ) = Subscriber(
        key = SubscriberPrimaryKey(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
        ),
        alarm = alarm,
    )

}
