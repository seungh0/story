package com.story.platform.core.domain.feed

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeedRepository : CoroutineCrudRepository<Feed, FeedPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberId(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyEventIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        subscriberId: String,
        eventId: Long,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyEventIdAsc(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyEventIdGreaterThanOrderByKeyEventIdAsc(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        subscriberId: String,
        eventId: Long,
        pageable: Pageable,
    ): Flow<Feed>

}
