package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.RandomGenerator.generateEnum
import com.story.platform.core.support.RandomGenerator.generateString

object SubscriptionDistributedFixture {

    fun create(
        serviceType: ServiceType = generateEnum(ServiceType::class.java),
        subscriptionType: SubscriptionType = generateEnum(SubscriptionType::class.java),
        targetId: String = generateString(),
        subscriberId: String = generateString(),
    ) = SubscriptionDistributed(
        key = SubscriptionDistributedPrimaryKey.of(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        ),
    )

}
