package com.story.core.domain.post

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class PostLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#workspaceId} + ':componentId:' + {#componentId} + ':spaceId:' + {#spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
        targetCacheStrategies = [CacheStrategy.LOCAL]
    )
    suspend fun evictPost(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        postId: PostId,
    ) {
    }

}
