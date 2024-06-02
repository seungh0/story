package com.story.core.domain.feed

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedReadRepository {

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberId(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Feed>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Slice<Feed>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Feed>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Slice<Feed>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Feed>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotIdAndKeySubscriberIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Feed>

}
