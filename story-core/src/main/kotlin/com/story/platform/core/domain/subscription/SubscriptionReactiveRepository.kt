package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionReactiveRepository : ReactiveCassandraRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdLessThan(
        serviceType: com.story.platform.core.common.enums.ServiceType,
        subscriptionType: String,
        targetId: String,
        slotNo: Long,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdAndKeySubscriberIdLessThan(
        serviceType: com.story.platform.core.common.enums.ServiceType,
        subscriptionType: String,
        targetId: String,
        slotNo: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdAndKeySubscriberIdGreaterThanOrderByKeySubscriberIdAsc(
        serviceType: com.story.platform.core.common.enums.ServiceType,
        subscriptionType: String,
        targetId: String,
        slotNo: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

}
