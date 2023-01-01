package com.story.datacenter.core.domain.subscription

import com.story.datacenter.core.common.enums.ServiceType
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionReverseReactiveRepository :
    ReactiveCassandraRepository<SubscriptionReverse, SubscriptionReversePrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberId(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<SubscriptionReverse>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThan(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<SubscriptionReverse>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<SubscriptionReverse>

}
