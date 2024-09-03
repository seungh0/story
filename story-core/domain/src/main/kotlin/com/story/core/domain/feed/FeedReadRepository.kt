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

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyPriorityLessThan(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        priority: Long,
        pageable: Pageable,
    ): Slice<Feed>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdOrderByKeyPriortyAsc(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        pageable: Pageable,
    ): Slice<Feed>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyPriorityGreaterThanOrderByKeyPriorityAsc(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        priority: Long,
        pageable: Pageable,
    ): Slice<Feed>

}
