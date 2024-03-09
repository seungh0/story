package com.story.core.domain.post

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable

interface PostRepository : CassandraBasicRepository<Post, PostPrimaryKey> {

    @Query(
        """
        update post_v1
        set metadata[:metadataType] = :value
        where workspace_id = :#{#key.workspaceId}
        and component_id = :#{#key.componentId} and space_id = :#{#key.spaceId} and parent_id = :#{#key.parentId} and slot_id = :#{#key.slotId} and post_id = :#{#key.postId}
    """
    )
    suspend fun putMetadata(key: PostPrimaryKey, metadataType: PostMetadataType, value: String)

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

    suspend fun deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
    )

}
