package com.story.core.domain.feed

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface FeedSubscriberRepository : CassandraBasicRepository<FeedSubscriberEntity, FeedSubscriberPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        pageable: Pageable,
    ): Flow<FeedSubscriberEntity>

    fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotIdAndKeySubscriberIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<FeedSubscriberEntity>

}
