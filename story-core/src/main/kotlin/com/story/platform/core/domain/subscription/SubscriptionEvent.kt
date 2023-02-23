package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType

data class SubscriptionEvent(
    val type: SubscriptionEventType,
    val serviceType: ServiceType,
    val subscriptionType: SubscriptionType,
    val subscriberId: String,
    val targetId: String,
)

enum class SubscriptionEventType {

    SUBSCRIPTION,
    UN_SUBSCRIPTION,

}
