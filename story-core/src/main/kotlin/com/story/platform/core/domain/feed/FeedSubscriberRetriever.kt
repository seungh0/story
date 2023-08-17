package com.story.platform.core.domain.feed

import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.CursorRequest
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedSubscriberRetriever(
    private val feedSubscriberRepository: FeedSubscriberRepository,
) {

    suspend fun listFeedSubscribersBySlot(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        cursorRequest: CursorRequest,
    ): CursorResult<FeedSubscriberResponse, String> {
        val feedSubscribers = listSubscribers(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            eventKey = eventKey,
            slotId = slotId,
            subscriberId = cursorRequest.cursor,
            pageSize = cursorRequest.pageSize,
        )
        return CursorResult.of(
            data = feedSubscribers.subList(0, cursorRequest.pageSize.coerceAtMost(feedSubscribers.size))
                .map { feedSubscriber -> FeedSubscriberResponse.of(feedSubscriber) },
            cursor = getCursor(feedSubscribers = feedSubscribers, pageSize = cursorRequest.pageSize)
        )
    }

    private suspend fun listSubscribers(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        subscriberId: String?,
        pageSize: Int,
    ): List<FeedSubscriber> {
        if (subscriberId == null) {
            return feedSubscriberRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                eventKey = eventKey,
                slotId = slotId,
                pageable = CassandraPageRequest.first(pageSize + 1),
            ).toList()
        }
        return feedSubscriberRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotIdAndKeySubscriberIdLessThan(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            eventKey = eventKey,
            slotId = slotId,
            subscriberId = subscriberId,
            pageable = CassandraPageRequest.first(pageSize + 1),
        ).toList()
    }

    private suspend fun getCursor(feedSubscribers: List<FeedSubscriber>, pageSize: Int): Cursor<String> {
        if (feedSubscribers.size > pageSize) {
            val cursor = feedSubscribers.subList(0, pageSize.coerceAtMost(feedSubscribers.size))
                .lastOrNull()?.key?.subscriberId
            return Cursor.of(cursor = cursor)
        }
        return Cursor.noMore()
    }

}
