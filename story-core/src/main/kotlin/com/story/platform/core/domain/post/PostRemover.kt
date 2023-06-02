package com.story.platform.core.domain.post

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostRemover(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
) {

    suspend fun remove(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
    ) {
        val postReverse =
            postReverseRepository.findByKeyServiceTypeAndKeyAccountIdAndKeyPostIdAndKeySpaceTypeAndKeySpaceId(
                serviceType = postSpaceKey.serviceType,
                accountId = accountId,
                postId = postId,
                spaceType = postSpaceKey.spaceType,
                spaceId = postSpaceKey.spaceId,
            ) ?: return

        val post = postRepository.findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = postReverse.slotId,
            postId = postId,
        )

        reactiveCassandraOperations.batchOps()
            .delete(post, postReverse)
            .executeCoroutine()
    }

}
