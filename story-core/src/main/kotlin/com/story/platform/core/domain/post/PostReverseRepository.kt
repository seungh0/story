package com.story.platform.core.domain.post

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostReverseRepository : CoroutineCrudRepository<PostReverse, PostReversePrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyAccountIdAndKeyPostIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        accountId: String,
        postId: Long,
        spaceId: String,
    ): PostReverse?

}
