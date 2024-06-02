package com.story.core.domain.subscription

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriberRepository : CassandraBasicRepository<SubscriberEntity, SubscriberPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdAsc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<SubscriberEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanOrderByKeySubscriberIdAsc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<SubscriberEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdAsc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: CassandraPageRequest,
    ): Flow<SubscriberEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThanOrderByKeySubscriberIdDesc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<SubscriberEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        pageable: CassandraPageRequest,
    ): Flow<SubscriberEntity>

    suspend fun deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotId(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
    )

}
