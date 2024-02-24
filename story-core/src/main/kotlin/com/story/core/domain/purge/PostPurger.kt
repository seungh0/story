package com.story.core.domain.purge

import com.story.core.common.distribution.DistributionKey
import com.story.core.domain.post.PostDistributionKey
import com.story.core.domain.post.PostRepository
import com.story.core.domain.post.PostReverse
import com.story.core.domain.post.PostReverseRepository
import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostPurger(
    private val postReverseRepository: PostReverseRepository,
    private val postRepository: PostRepository,
) : Purger {

    override fun targetResourceId(): ResourceId = ResourceId.POSTS

    override fun distributeKeys(): Collection<DistributionKey> = PostDistributionKey.ALL_KEYS

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

            postReverseRepository.deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
            )

            deletedCount += postReverses.size

            pageable = postReverses.nextPageable()
        } while (postReverses.hasNext())

        return deletedCount
    }

    data class PostPartitionKey(
        val workspaceId: String,
        val componentId: String,
        val spaceId: String,
        val parentId: String,
        val slotId: Long,
    ) {

        companion object {
            fun from(postReverse: PostReverse) = PostPartitionKey(
                workspaceId = postReverse.key.workspaceId,
                componentId = postReverse.key.componentId,
                spaceId = postReverse.key.spaceId,
                parentId = postReverse.key.parentId,
                slotId = postReverse.slotId,
            )
        }

    }

}
