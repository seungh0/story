package com.story.core.domain.post

import com.story.core.common.logger.LoggerExtension.log
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import org.springframework.stereotype.Service

@Service
class PostLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#workspaceId} + ':componentId:' + {#componentId} + ':spaceId:' + {#spaceId} + ':parentId:' + {#postId.parentId} + ':postId:' + {#postId.postId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    suspend fun evictPost(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        postId: PostKey,
    ) {
        log.debug { "Post 캐시가 만료됩니다 [workspaceId: $workspaceId componentId: $componentId spaceId: $spaceId postId: $postId]" }
    }

}
