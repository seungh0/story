package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionReactiveRepository :
    ReactiveCassandraRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdOrderByKeyTargetIdDesc(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberId(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdDesc(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ): Subscription?

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThan(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<Subscription>

}
