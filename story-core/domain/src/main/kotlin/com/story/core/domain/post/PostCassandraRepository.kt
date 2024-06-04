package com.story.core.domain.post

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable

interface PostCassandraRepository : CassandraBasicRepository<PostEntity, PostPrimaryKey> {

    @Query(
        """
        update post_v1
        set metadata[:metadataType] = :value
        where workspace_id = :#{#key.workspaceId}
        and component_id = :#{#key.componentId} and space_id = :#{#key.spaceId} and parent_id = :#{#key.parentId} and slot_id = :#{#key.slotId} and post_no = :#{#key.postNo}
    """
    )
    suspend fun putMetadata(key: PostPrimaryKey, metadataType: PostMetadataType, value: String)

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postNo: Long,
    ): PostEntity?

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        pageable: Pageable,
    ): Flow<PostEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoLessThan(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postNo: Long,
        pageable: Pageable,
    ): Flow<PostEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdOrderByKeyPostNoAsc(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        pageable: Pageable,
    ): Flow<PostEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoGreaterThanOrderByKeyPostNoAsc(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postNo: Long,
        pageable: Pageable,
    ): Flow<PostEntity>

    suspend fun deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
    )

}
