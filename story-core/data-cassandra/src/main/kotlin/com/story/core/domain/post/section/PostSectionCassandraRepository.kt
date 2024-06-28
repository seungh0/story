package com.story.core.domain.post.section

import com.story.core.support.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow

interface PostSectionCassandraRepository : CassandraBasicRepository<PostSectionEntity, PostSectionPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postNo: Long,
    ): Flow<PostSectionEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoIn(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postNos: Collection<Long>,
    ): Flow<PostSectionEntity>

}
