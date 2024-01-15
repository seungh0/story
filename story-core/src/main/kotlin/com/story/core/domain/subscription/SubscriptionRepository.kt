package com.story.core.domain.subscription

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionRepository :
    CoroutineCrudRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetId(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
    ): Subscription?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        pageable: Pageable,
    ): Slice<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Subscription>

}
