package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.RandomGenerator.generateEnum
import com.story.platform.core.support.RandomGenerator.generateLong
import com.story.platform.core.support.RandomGenerator.generateString

object SubscribersCounterFixture {

    fun create(
        serviceType: ServiceType = generateEnum(ServiceType::class.java),
        subscriptionType: SubscriptionType = generateEnum(SubscriptionType::class.java),
        targetId: String = generateString(),
        count: Long = generateLong(),
    ) = SubscribersCounter(
        key = SubscribersCounterPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        ),
        count = count,
    )

}
