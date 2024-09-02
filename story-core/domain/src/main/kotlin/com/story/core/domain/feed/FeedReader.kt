package com.story.core.domain.feed

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
        componentId: String,
        ownerId: String,
        cursorRequest: CursorRequest,
    ): Slice<Feed, String> {
        val feeds = listNextFeeds(cursorRequest, workspaceId, componentId, ownerId)

        return Slice.of(
            data = feeds.content.subList(0, cursorRequest.pageSize.coerceAtMost(feeds.numberOfElements)),
            cursor = CursorUtils.getCursor(
                listWithNextCursor = feeds.content,
                pageSize = cursorRequest.pageSize,
                keyGenerator = { feed -> feed?.sortKey?.toString() }
            )
        )
    }

    private suspend fun listNextFeeds(
        cursorRequest: CursorRequest,
        workspaceId: String,
        componentId: String,
        ownerId: String,
    ): org.springframework.data.domain.Slice<Feed> {
        if (cursorRequest.cursor.isNullOrBlank()) {
            return feedReadRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerId(
                workspaceId = workspaceId,
                componentId = componentId,
                ownerId = ownerId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize),
            )
        }
        return feedReadRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeySortKeyLessThan(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            sortKey = cursorRequest.cursor.toLong(),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize),
        )
    }

}
