package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionReverseReactiveRepository :
    ReactiveCassandraRepository<SubscriptionReverse, SubscriptionReversePrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdDesc(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<SubscriptionReverse>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
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
