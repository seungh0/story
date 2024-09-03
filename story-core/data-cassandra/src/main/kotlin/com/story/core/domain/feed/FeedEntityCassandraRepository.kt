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

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeySortKeyLessThan(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        sortKey: Long,
        pageable: Pageable,
    ): Slice<FeedEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdOrderByKeySortKeyAsc(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        pageable: Pageable,
    ): Slice<FeedEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeySortKeyGreaterThanOrderByKeySortKeyAsc(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        sortKey: Long,
        pageable: Pageable,
    ): Slice<FeedEntity>

}
