package com.story.core.domain.post

import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import org.springframework.stereotype.Service

@Service
class PostLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#workspaceId} + ':componentId:' + {#componentId} + ':spaceId:' + {#spaceId} + ':parentId:' + {#postId.parentKey} + ':postId:' + {#postId.postId}",
        targetCacheStrategies = [CacheStrategy.LOCAL]
    )
    suspend fun evictPost(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        postId: PostKey,
    ) {
    }

}
