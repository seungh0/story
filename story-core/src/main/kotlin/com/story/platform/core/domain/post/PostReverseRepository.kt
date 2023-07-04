package com.story.platform.core.domain.post

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostReverseRepository : CoroutineCrudRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyAccountIdAndKeyPostIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        accountId: String,
        postId: Long,
        spaceId: String,
    ): PostReverse?

}
