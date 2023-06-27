package com.story.platform.core.domain.subscription

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionRepository :
    CoroutineCrudRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdAndKeyTargetId(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
    ): Subscription?

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Subscription>

}
