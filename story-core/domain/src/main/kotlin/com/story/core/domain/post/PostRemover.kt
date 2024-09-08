package com.story.core.domain.post

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheType
import com.story.core.support.lock.DistributedLock
import com.story.core.support.lock.DistributedLockType
import org.springframework.stereotype.Service

@Service
class PostRemover(
    private val postRepository: PostRepository,
) {

    @DistributedLock(
        lockType = DistributedLockType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
    )
    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
    )
    suspend fun removePost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostId,
    ) {
        postRepository.delete(
            postSpaceKey = postSpaceKey,
            ownerId = ownerId,
            postId = postId,
        )
    }

}
