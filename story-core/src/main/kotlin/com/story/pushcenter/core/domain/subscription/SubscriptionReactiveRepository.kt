package com.story.pushcenter.core.domain.subscription

import com.story.pushcenter.core.common.enums.ServiceType
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionReactiveRepository : ReactiveCassandraRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdLessThan(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        slotNo: Long,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdAndKeySubscriberIdLessThan(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        slotNo: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdAndKeySubscriberIdGreaterThanOrderByKeySubscriberIdAsc(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        slotNo: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

}
