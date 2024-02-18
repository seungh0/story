package com.story.core.domain.post

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface PostRepository : CassandraBasicRepository<Post, PostPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postId: Long,
    ): Post?

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        pageable: Pageable,
    ): Flow<Post>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostIdLessThan(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Flow<Post>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdOrderByKeyPostIdAsc(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        pageable: Pageable,
    ): Flow<Post>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Flow<Post>

}
