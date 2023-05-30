package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.RandomGenerator.generateBoolean
import com.story.platform.core.support.RandomGenerator.generateEnum
import com.story.platform.core.support.RandomGenerator.generateLong
import com.story.platform.core.support.RandomGenerator.generateString

object SubscriptionFixture {

    fun create(
        serviceType: ServiceType = generateEnum(ServiceType::class.java),
        subscriptionType: SubscriptionType = generateEnum(SubscriptionType::class.java),
        targetId: String = generateString(),
        slotId: Long = generateLong(),
        subscriberId: String = generateString(),
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        alarm: Boolean = generateBoolean(),
    ) = Subscription(
        key = SubscriptionPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        ),
        slotId = slotId,
        status = status,
        alarm = alarm,
    )

}
