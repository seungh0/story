package com.story.platform.core.domain.subscription

import com.story.platform.core.support.RandomGenerator.generateBoolean
import com.story.platform.core.support.RandomGenerator.generateLong
import com.story.platform.core.support.RandomGenerator.generateString

object SubscriptionFixture {

    fun create(
        workspaceId: String = generateString(),
        componentId: String = generateString(),
        targetId: String = generateString(),
        slotId: Long = generateLong(),
        subscriberId: String = generateString(),
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        alarmEnabled: Boolean = generateBoolean(),
    ) = Subscription(
        key = SubscriptionPrimaryKey.of(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        ),
        slotId = slotId,
        status = status,
        alarmEnabled = alarmEnabled,
    )

}
