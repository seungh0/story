package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriberReactiveRepository : ReactiveCassandraRepository<Subscriber, SubscriberPrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanEqual(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThan(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdLessThan(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdLessThan(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        pageable: CassandraPageRequest,
    ): Slice<Subscriber>

}
