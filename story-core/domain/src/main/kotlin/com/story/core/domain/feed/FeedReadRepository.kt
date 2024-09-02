package com.story.core.domain.feed

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedReadRepository {

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerId(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        pageable: Pageable,
    ): Slice<Feed>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeySortKeyLessThan(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        sortKey: Long,
        pageable: Pageable,
    ): Slice<Feed>

}
