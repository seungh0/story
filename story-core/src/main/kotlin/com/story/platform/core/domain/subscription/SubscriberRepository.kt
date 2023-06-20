package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriberRepository : CoroutineCrudRepository<Subscriber, SubscriberPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotId(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanEqual(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThan(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdLessThan(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Subscriber>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdLessThan(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: CassandraPageRequest,
    ): Slice<Subscriber>

}
