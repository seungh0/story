package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionReactiveRepository : ReactiveCassandraRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdLessThanOrderByKeySubscriberIdDesc(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdGreaterThanOrderByKeySubscriberIdAsc(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

}
