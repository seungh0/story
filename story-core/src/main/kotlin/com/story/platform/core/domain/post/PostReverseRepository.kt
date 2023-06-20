package com.story.platform.core.domain.post

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostReverseRepository : CoroutineCrudRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyAccountIdAndKeyPostIdAndKeySpaceId(
        workspaceId: String,
        accountId: String,
        postId: Long,
        spaceId: String,
    ): PostReverse?

}
