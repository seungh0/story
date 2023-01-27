package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType

object SubscriptionReverseFixture {

    fun create(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
    ) = SubscriptionReverse(
        key = SubscriptionReversePrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        ),
        slotId = slotId,
    )

}
