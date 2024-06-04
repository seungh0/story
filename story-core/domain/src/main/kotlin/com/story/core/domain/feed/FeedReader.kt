package com.story.core.domain.feed

import com.story.core.common.model.CursorDirection
import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorRequest
import com.story.core.common.utils.CursorUtils
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedReader(
    private val feedReadRepository: FeedReadRepository,
) {

    suspend fun listFeeds(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): Slice<Feed, String> {
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
                subscriberId = subscriberId,
                cursorRequest = cursorRequest,
            )
        }

        return Slice.of(
            data = feeds.content.subList(0, cursorRequest.pageSize.coerceAtMost(feeds.numberOfElements)),
            cursor = CursorUtils.getCursor(
                listWithNextCursor = feeds.content,
                pageSize = cursorRequest.pageSize,
                keyGenerator = { feed -> feed?.feedId?.toString() }
            )
        )
    }

    private suspend fun listNextFeeds(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): org.springframework.data.domain.Slice<Feed> {
        if (cursorRequest.cursor.isNullOrBlank()) {
            return feedReadRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberId(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize),
            )
        }
        return feedReadRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdLessThan(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            feedId = cursorRequest.cursor.toLong(),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize),
        )
    }

    private suspend fun listPreviousFeeds(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): org.springframework.data.domain.Slice<Feed> {
        if (cursorRequest.cursor.isNullOrBlank()) {
            return feedReadRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdOrderByKeyFeedIdAsc(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        }
        return feedReadRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            feedId = cursorRequest.cursor.toLong(),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        )
    }

}
