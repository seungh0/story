package com.story.platform.core.domain.post

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostReverseRepository : CoroutineCrudRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyAccountIdAndKeyPostIdAndKeySpaceTypeAndKeySpaceId(
        workspaceId: String,
        accountId: String,
        postId: Long,
        spaceType: PostSpaceType,
        spaceId: String,
    ): PostReverse?

}
