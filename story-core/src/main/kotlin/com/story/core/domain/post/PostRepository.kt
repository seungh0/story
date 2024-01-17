package com.story.core.domain.post

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface PostRepository : CassandraBasicRepository<Post, PostPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
    ): Post?

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Flow<Post>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Flow<Post>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        pageable: Pageable,
    ): Flow<Post>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        slotId: Long,
        postId: Long,
        pageable: Pageable,
    ): Flow<Post>

}
