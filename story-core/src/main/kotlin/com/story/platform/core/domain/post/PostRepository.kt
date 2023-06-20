package com.story.platform.core.domain.post

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostRepository : CoroutineCrudRepository<Post, PostPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeySpaceIdAndKeySlotIdAndKeyPostId(
        workspaceId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
    ): Post?

    suspend fun findAllByKeyWorkspaceIdAndKeySpaceIdAndKeySlotId(
        workspaceId: String,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
        workspaceId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
        workspaceId: String,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
        workspaceId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Slice<Post>

}
