package com.story.core.domain.post

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostReverseRepository : CoroutineCrudRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyPostIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        ownerId: String,
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
