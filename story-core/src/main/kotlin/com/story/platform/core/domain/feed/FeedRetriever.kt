package com.story.platform.core.domain.feed

import com.story.platform.core.common.distribution.XLargeDistributionKey
import com.story.platform.core.common.model.ContentsWithCursor
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.CursorUtils
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
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): ContentsWithCursor<FeedResponse<out BaseEvent>, String> {
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

        return ContentsWithCursor.of(
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
            return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberId(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                distributionKey = XLargeDistributionKey.makeKey(subscriberId).key,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize),
            )
        }
        return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyFeedIdLessThan(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            distributionKey = XLargeDistributionKey.makeKey(subscriberId).key,
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
            return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyFeedIdAsc(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                distributionKey = XLargeDistributionKey.makeKey(targetId).key,
                subscriberId = targetId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        }
        return feedRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            distributionKey = XLargeDistributionKey.makeKey(targetId).key,
            subscriberId = targetId,
            feedId = cursorRequest.cursor.toLong(),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        )
    }

}
