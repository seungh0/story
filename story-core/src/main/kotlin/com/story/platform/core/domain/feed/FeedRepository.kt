package com.story.platform.core.domain.feed

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeedRepository : CoroutineCrudRepository<Feed, FeedPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeyTargetId(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeyTargetIdAndKeyEventIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        targetId: String,
        eventId: Long,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeyTargetIdOrderByKeyEventIdAsc(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeyTargetIdAndKeyEventIdGreaterThanOrderByKeyEventIdAsc(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        targetId: String,
        eventId: Long,
        pageable: Pageable,
    ): Flow<Feed>

}
