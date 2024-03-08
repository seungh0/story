package com.story.core.domain.post

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface PostReverseRepository : CassandraBasicRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyPostIdAndKeyParentIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        ownerId: String,
        postId: Long,
        parentId: String,
        spaceId: String,
    ): PostReverse?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        pageable: Pageable,
    ): Slice<PostReverse>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerId(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        ownerId: String,
        pageable: Pageable,
    ): Flow<PostReverse>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyPostIdLessThan(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        ownerId: String,
        postId: Long,
        pageable: Pageable,
    ): Flow<PostReverse>

    suspend fun deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
    )

}
