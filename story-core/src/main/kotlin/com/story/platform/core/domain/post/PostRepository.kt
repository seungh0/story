package com.story.platform.core.domain.post

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostRepository : CoroutineCrudRepository<Post, PostPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
        workspaceId: String,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        postId: Long,
    ): Post?

    suspend fun findAllByKeyWorkspaceIdAndKeySpaceTypeAndKeySpaceIdAndKeySlotId(
        workspaceId: String,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
        workspaceId: String,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
        workspaceId: String,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Post>

    suspend fun findAllByKeyWorkspaceIdAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
        workspaceId: String,
        spaceType: PostSpaceType,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Slice<Post>

}
