package com.story.core.domain.feed

import com.story.core.support.cassandra.CassandraBasicRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedSubscriberCassandraRepository : CassandraBasicRepository<FeedSubscriberEntity, FeedSubscriberPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<FeedSubscriberEntity>

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotIdAndKeySubscriberIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<FeedSubscriberEntity>

}
