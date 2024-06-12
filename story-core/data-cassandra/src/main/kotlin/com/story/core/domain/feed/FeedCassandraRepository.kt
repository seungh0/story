package com.story.core.domain.feed

import com.story.core.support.cassandra.CassandraBasicRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedCassandraRepository : CassandraBasicRepository<FeedEntity, FeedPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberId(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<FeedEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Slice<FeedEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<FeedEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Slice<FeedEntity>

}
