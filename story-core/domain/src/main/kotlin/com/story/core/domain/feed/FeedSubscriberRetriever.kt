package com.story.core.domain.feed

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorRequest
import com.story.core.common.utils.CursorUtils
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedSubscriberRetriever(
    private val feedReadRepository: FeedReadRepository,
) {

    suspend fun listFeedSubscribersBySlot(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        cursorRequest: CursorRequest,
    ): Slice<Feed, String> {
        val feedSubscribers = listSubscribers(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            eventKey = eventKey,
            slotId = slotId,
            subscriberId = cursorRequest.cursor,
            pageSize = cursorRequest.pageSize,
        )
        return Slice.of(
            data = feedSubscribers.subList(0, cursorRequest.pageSize.coerceAtMost(feedSubscribers.size)),
            cursor = CursorUtils.getCursor(
                listWithNextCursor = feedSubscribers, pageSize = cursorRequest.pageSize,
                keyGenerator = { feedSubscriber -> feedSubscriber?.subscriberId }
            )
        )
    }

    private suspend fun listSubscribers(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        subscriberId: String?,
        pageSize: Int,
    ): List<Feed> {
        if (subscriberId == null) {
            return feedReadRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                eventKey = eventKey,
                slotId = slotId,
                pageable = CassandraPageRequest.first(pageSize + 1),
            ).toList()
        }
        return feedReadRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotIdAndKeySubscriberIdLessThan(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            eventKey = eventKey,
            slotId = slotId,
            subscriberId = subscriberId,
            pageable = CassandraPageRequest.first(pageSize + 1),
        ).toList()
    }

}
