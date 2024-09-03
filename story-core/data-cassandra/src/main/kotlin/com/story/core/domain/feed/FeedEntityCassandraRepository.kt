package com.story.core.domain.feed

import com.story.core.support.cassandra.CassandraBasicRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedEntityCassandraRepository : CassandraBasicRepository<FeedEntity, FeedEntityPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerId(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        pageable: Pageable,
    ): Slice<FeedEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyPriorityLessThan(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        priority: Long,
        pageable: Pageable,
    ): Slice<FeedEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdOrderByKeyPriorityAsc(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        pageable: Pageable,
    ): Slice<FeedEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyPriorityGreaterThanOrderByKeyPriorityAsc(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        priority: Long,
        pageable: Pageable,
    ): Slice<FeedEntity>

}
