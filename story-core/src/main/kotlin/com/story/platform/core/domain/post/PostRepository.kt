package com.story.platform.core.domain.post

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostRepository : CoroutineCrudRepository<Post, PostPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
    ): Post?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Slice<Post>

}
