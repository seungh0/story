package com.story.core.domain.feed

import com.story.core.support.cassandra.CassandraBasicRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedReverseEntityCassandraRepository :
    CassandraBasicRepository<FeedReverseEntity, FeedReverseEntityPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyChannelId(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        channelId: String,
        pageable: Pageable,
    ): Slice<FeedReverseEntity>

}
