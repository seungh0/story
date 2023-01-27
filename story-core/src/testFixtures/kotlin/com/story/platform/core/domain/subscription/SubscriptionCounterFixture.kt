package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType

object SubscriptionCounterFixture {

    fun create(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        count: Long,
    ) = SubscriptionCounter(
        key = SubscriptionCounterPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        ),
        count = count,
    )

}
