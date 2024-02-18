package com.story.core.domain.post

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface PostReverseRepository : CassandraBasicRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyParentIdAndKeyPostIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        ownerId: String,
        parentId: String,
        postId: Long,
        spaceId: String,
    ): PostReverse?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        pageable: Pageable,
    ): Slice<PostReverse>

}
