package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType

object SubscriptionFixture {

    fun create(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        extraJson: String? = null,
    ) = Subscription(
        key = SubscriptionPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
        ),
        extraJson = extraJson,
    )

}
