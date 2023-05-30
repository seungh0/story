package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionRepository :
    CoroutineCrudRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ): Subscription?

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdDesc(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdAsc(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<Subscription>

}
