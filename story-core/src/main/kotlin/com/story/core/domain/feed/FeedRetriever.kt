package com.story.core.domain.feed

import com.story.core.common.model.CursorDirection
import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorRequest
import com.story.core.common.utils.CursorUtils
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
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): Slice<FeedResponse, String> {
        val feeds = when (cursorRequest.direction) {
            CursorDirection.NEXT -> listNextFeeds(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriberId = subscriberId,
                cursorRequest = cursorRequest,
            )

            CursorDirection.PREVIOUS -> listPreviousFeeds(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                targetId = subscriberId,
                cursorRequest = cursorRequest,
            )
        }.toList()

        return Slice.of(
            data = feeds.subList(0, cursorRequest.pageSize.coerceAtMost(feeds.size))
                .map { feed -> FeedResponse.of(feed) },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = feeds,
                pageSize = cursorRequest.pageSize,
                keyGenerator = { feed -> feed?.key?.feedId?.toString() }
            )
        )
    }

    private fun listNextFeeds(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): Flow<Feed> {
        if (cursorRequest.cursor.isNullOrBlank()) {
            return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberId(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize),
            )
        }
        return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdLessThan(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            feedId = cursorRequest.cursor.toLong(),
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
            return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdOrderByKeyFeedIdAsc(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriberId = targetId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        }
        return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = targetId,
            feedId = cursorRequest.cursor.toLong(),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        )
    }

}
