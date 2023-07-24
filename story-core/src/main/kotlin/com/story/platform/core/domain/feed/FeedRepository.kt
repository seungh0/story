package com.story.platform.core.domain.feed

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeedRepository : CoroutineCrudRepository<Feed, FeedPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyTargetId(
        workspaceId: String,
        feedComponentId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyTargetIdAndKeyEventIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        targetId: String,
        eventId: Long,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyTargetIdOrderByKeyEventIdAsc(
        workspaceId: String,
        feedComponentId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyTargetIdAndKeyEventIdGreaterThanOrderByKeyEventIdAsc(
        workspaceId: String,
        feedComponentId: String,
        targetId: String,
        eventId: Long,
        pageable: Pageable,
    ): Flow<Feed>

}
