package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionReverseReactiveRepository :
    ReactiveCassandraRepository<SubscriptionReverse, SubscriptionReversePrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThanEqual(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<SubscriptionReverse>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdGreaterThanEqual(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<SubscriptionReverse>

    suspend fun findByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        targetId: String,
    ): SubscriptionReverse?

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThanEqual(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<SubscriptionReverse>

}
