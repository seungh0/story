package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.RandomGenerator.generateEnum
import com.story.platform.core.support.RandomGenerator.generateString

object SubscriptionDistributorFixture {

    fun create(
        serviceType: ServiceType = generateEnum(ServiceType::class.java),
        subscriptionType: SubscriptionType = generateEnum(SubscriptionType::class.java),
        distributedKey: String? = null,
        targetId: String = generateString(),
        subscriberId: String = generateString(),
    ) = SubscriberDistributed(
        key = SubscriberDistributedPrimaryKey(
            serviceType = serviceType,
            distributedKey = distributedKey
                ?: SubscriberDistributedKeyGenerator.generate(subscriberId = subscriberId),
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        ),
    )

}
