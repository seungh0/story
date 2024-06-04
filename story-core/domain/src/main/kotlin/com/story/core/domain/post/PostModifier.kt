package com.story.core.domain.post

import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.lock.DistributedLock
import com.story.core.infrastructure.lock.DistributedLockType
import org.springframework.stereotype.Service

@Service
class PostModifier(
    private val postRepository: PostRepository,
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
    suspend fun patchPost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostId,
        title: String?,
        sections: List<PostSectionContentCommand>?,
        extra: Map<String, String>?,
    ): PostPatchResponse {
        return postRepository.modify(
            postSpaceKey = postSpaceKey,
            parentId = postId.parentPostId(),
            postNo = postId.postNo,
            ownerId = ownerId,
            title = title,
            sections = sections,
            extra = extra,
        )
    }

    @DistributedLock(
        lockType = DistributedLockType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
    )
    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun putMetadata(
        postSpaceKey: PostSpaceKey,
        postId: PostId,
        metadataType: PostMetadataType,
        value: Any,
    ): Boolean {
        postRepository.putMetadata(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            postId = postId,
            parentId = postId.parentPostId(),
            metadataType = metadataType,
            value = value,
        )
        return true
    }

}
