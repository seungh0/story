package com.story.platform.core.domain.feed

import com.story.platform.core.common.distribution.XLargeDistributionKey
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.event.BaseEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedRetriever(
    private val feedRepository: FeedRepository,
) {

    suspend fun listFeeds(
        workspaceId: String,
        feedComponentId: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<FeedResponse<out BaseEvent>, String> {
        val feeds = when (cursorRequest.direction) {
            CursorDirection.NEXT -> listNextFeeds(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )

            CursorDirection.PREVIOUS -> listPreviousFeeds(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )
        }.toList()

        return CursorResult.of(
            data = feeds.subList(0, cursorRequest.pageSize.coerceAtMost(feeds.size))
                .map { feed -> FeedResponse.of(feed) },
            cursor = getCursor(feeds = feeds, pageSize = cursorRequest.pageSize)
        )
    }

    private fun listNextFeeds(
        workspaceId: String,
        feedComponentId: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): Flow<Feed> {
        if (cursorRequest.cursor.isNullOrBlank()) {
            return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeyTargetId(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                distributionKey = XLargeDistributionKey.fromKey(targetId).key,
                targetId = targetId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize),
            )
        }
        return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeyTargetIdAndKeyEventIdLessThan(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            distributionKey = XLargeDistributionKey.fromKey(targetId).key,
            targetId = targetId,
            eventId = cursorRequest.cursor.toLong(),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize),
        )
    }

    private fun listPreviousFeeds(
        workspaceId: String,
        feedComponentId: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): Flow<Feed> {
        if (cursorRequest.cursor.isNullOrBlank()) {
            return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeyTargetIdOrderByKeyEventIdAsc(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                distributionKey = XLargeDistributionKey.fromKey(targetId).key,
                targetId = targetId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        }
        return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeyTargetIdAndKeyEventIdGreaterThanOrderByKeyEventIdAsc(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            distributionKey = XLargeDistributionKey.fromKey(targetId).key,
            targetId = targetId,
            eventId = cursorRequest.cursor.toLong(),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        )
    }

    private suspend fun getCursor(feeds: List<Feed>, pageSize: Int): Cursor<String> {
        if (feeds.size > pageSize) {
            return Cursor.of(
                cursor = feeds.subList(0, pageSize.coerceAtMost(feeds.size)).lastOrNull()?.key?.eventId?.toString()
            )
        }
        return Cursor.noMore()
    }

}
