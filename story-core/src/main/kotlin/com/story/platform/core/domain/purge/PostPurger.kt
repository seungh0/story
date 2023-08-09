package com.story.platform.core.domain.purge

import com.story.platform.core.common.distribution.DistributionKey
import com.story.platform.core.common.distribution.XLargeDistributionKey
import com.story.platform.core.domain.post.PostPrimaryKey
import com.story.platform.core.domain.post.PostRepository
import com.story.platform.core.domain.post.PostReverseRepository
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostPurger(
    private val postReverseRepository: PostReverseRepository,
    private val postRepository: PostRepository,
) : Purger {

    override fun targetResourceId(): ResourceId = ResourceId.POSTS

    override fun distributeKeys(): Collection<DistributionKey> = XLargeDistributionKey.ALL_KEYS

    override suspend fun clear(workspaceId: String, componentId: String, distributionKey: DistributionKey): Long {
        var pageable: Pageable = CassandraPageRequest.first(500)
        var deletedCount = 0L
        do {
            val postReverses = postReverseRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
                pageable = pageable,
            )

            val postPrimaryKeys = postReverses.content.map { postReverse ->
                PostPrimaryKey.from(postReverse = postReverse)
            }

            postRepository.deleteAllById(postPrimaryKeys)
            postReverseRepository.deleteAllById(postReverses.content.map { postReverse -> postReverse.key })

            deletedCount += postReverses.size

            pageable = postReverses.nextPageable()
        } while (postReverses.hasNext())

        return deletedCount
    }

}
