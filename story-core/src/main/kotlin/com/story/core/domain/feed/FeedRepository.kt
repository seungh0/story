package com.story.core.domain.feed

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface FeedRepository : CassandraBasicRepository<Feed, FeedPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberId(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Flow<Feed>

}
