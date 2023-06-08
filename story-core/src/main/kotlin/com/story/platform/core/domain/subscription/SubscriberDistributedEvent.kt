package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType

data class SubscriberDistributedEvent(
    val serviceType: ServiceType,
    val subscriptionType: SubscriptionType,
    val targetId: String,
    val slot: Long,
    // TODO: 피드를 생성할때 필요한 정보들...
)
