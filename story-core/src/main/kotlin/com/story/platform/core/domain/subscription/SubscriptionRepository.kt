package com.story.platform.core.domain.subscription

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionRepository :
    CoroutineCrudRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ): Subscription?

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdDesc(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdAsc(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<Subscription>

}
