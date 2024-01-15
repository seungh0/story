package com.story.core.domain.subscription

import kotlinx.coroutines.flow.Flow
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriberRepository : CoroutineCrudRepository<Subscriber, SubscriberPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdAsc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Subscriber>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanOrderByKeySubscriberIdAsc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Subscriber>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdAsc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: CassandraPageRequest,
    ): Flow<Subscriber>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThanOrderByKeySubscriberIdDesc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Subscriber>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: CassandraPageRequest,
    ): Flow<Subscriber>

}
