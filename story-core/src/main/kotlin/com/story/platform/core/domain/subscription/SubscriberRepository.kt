package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriberRepository : CoroutineCrudRepository<Subscriber, SubscriberPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanEqual(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThan(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdLessThan(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdLessThan(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        pageable: CassandraPageRequest,
    ): Slice<Subscriber>

}
