package com.story.pushcenter.core.domain.subscription

import org.springframework.stereotype.Repository

@Repository
class SubscriptionSlotAllocator {

    suspend fun allocate(subscriptionType: String, targetId: String): Long {
        return 1L
    }

}
