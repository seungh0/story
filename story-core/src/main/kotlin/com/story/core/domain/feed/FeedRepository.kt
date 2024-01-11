package com.story.core.domain.feed

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

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyFeedIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Feed>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        distributionKey: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Flow<Feed>

}
