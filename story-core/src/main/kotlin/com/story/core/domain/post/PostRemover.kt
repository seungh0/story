package com.story.core.domain.post

import com.story.core.domain.post.section.PostSectionRepository
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.lock.DistributedLock
import com.story.core.infrastructure.lock.DistributedLockType
import kotlinx.coroutines.flow.toList
import org.apache.commons.lang3.StringUtils
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostRemover(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
    private val postSectionRepository: PostSectionRepository,
) {

    @DistributedLock(
        lockType = DistributedLockType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
    )
    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun removePost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostId,
    ) {
        val postReverse =
            postReverseRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeyOwnerIdAndKeyPostNoAndKeyParentIdAndKeySpaceId(
                workspaceId = postSpaceKey.workspaceId,
                componentId = postSpaceKey.componentId,
                distributionKey = PostDistributionKey.makeKey(ownerId),
                ownerId = ownerId,
                postNo = postId.postNo,
                parentId = postId.parentId ?: StringUtils.EMPTY,
                spaceId = postSpaceKey.spaceId,
            ) ?: return

        val post = postRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            slotId = postReverse.slotId,
            parentId = postId.parentId ?: StringUtils.EMPTY,
            postNo = postId.postNo,
        )

        val postSections = postSectionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            parentId = postReverse.key.parentId,
            postNo = postId.postNo,
            slotId = PostSlotAssigner.assign(postNo = postId.postNo)
        ).toList()

        reactiveCassandraOperations.batchOps()
            .delete(post)
            .delete(postReverse)
            .delete(postSections)
            .executeCoroutine()
    }

}
