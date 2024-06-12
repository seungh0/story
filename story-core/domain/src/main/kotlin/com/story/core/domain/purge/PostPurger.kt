package com.story.core.domain.purge

import com.story.core.common.distribution.DistributionKey
import com.story.core.domain.post.PostCassandraRepository
import com.story.core.domain.post.PostDistributionKey
import com.story.core.domain.post.PostPartitionKey
import com.story.core.domain.post.PostReverseCassandraRepository
import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostPurger(
    private val postReverseCassandraRepository: PostReverseCassandraRepository,
    private val postRepository: PostCassandraRepository,
) : Purger {

    override fun targetResourceId(): ResourceId = ResourceId.POSTS

    override fun distributeKeys(): Collection<DistributionKey> = PostDistributionKey.ALL_KEYS

    override suspend fun clear(workspaceId: String, componentId: String, distributionKey: DistributionKey): Long {
        var pageable: Pageable = CassandraPageRequest.first(500)
        var deletedCount = 0L
        do {
            val postReverses = postReverseCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
                pageable = pageable,
            )

            postReverses.content.groupBy { postReverse -> PostPartitionKey.from(postReverse) }.keys
                .forEach { key ->
                    postRepository.deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotId(
                        workspaceId = key.workspaceId,
                        componentId = key.componentId,
                        spaceId = key.spaceId,
                        parentId = key.parentId,
                        slotId = key.slotId,
                    )
                }

            postReverseCassandraRepository.deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
            )

            deletedCount += postReverses.size

            pageable = postReverses.nextPageable()
        } while (postReverses.hasNext())

        return deletedCount
    }

}