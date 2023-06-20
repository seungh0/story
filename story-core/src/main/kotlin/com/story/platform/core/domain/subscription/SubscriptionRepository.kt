package com.story.platform.core.domain.subscription

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionRepository :
    CoroutineCrudRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdAndKeyTargetId(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
    ): Subscription?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscription>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<Subscription>

}
