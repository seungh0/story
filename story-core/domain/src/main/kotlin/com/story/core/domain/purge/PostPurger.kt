package com.story.core.domain.purge

import com.story.core.common.distribution.DistributionKey
import com.story.core.domain.post.PostDistributionKey
import com.story.core.domain.post.PostRepository
import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class PostPurger(
    private val postRepository: PostRepository,
) : Purger {

    override fun targetResourceId(): ResourceId = ResourceId.POSTS

    override fun distributeKeys(): Collection<DistributionKey> = PostDistributionKey.ALL_KEYS

    override suspend fun clear(workspaceId: String, componentId: String, distributionKey: DistributionKey): Long {
        return postRepository.clear(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = distributionKey
        )
    }

}
