package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriptionSlotAllocator(
    private val stringRedisRepository: StringRedisRepository<SubscriptionSequenceKey, Long>,
) {

    suspend fun getCurrentSlot(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
    ): Long {
        return stringRedisRepository.get(
            key = SubscriptionSequenceKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )
        ) ?: FIRST_SLOT_ID
    }

    suspend fun allocate(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
    ): Long {
        val subscriptionSequenceKey = stringRedisRepository.incr(
            key = SubscriptionSequenceKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )
        )
        return subscriptionSequenceKey / SLOT_SIZE + FIRST_SLOT_ID
    }

    companion object {
        const val FIRST_SLOT_ID = 1L
        private const val SLOT_SIZE = 50_000
    }

}
