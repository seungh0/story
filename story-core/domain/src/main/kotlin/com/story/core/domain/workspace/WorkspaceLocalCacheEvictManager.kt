package com.story.core.domain.workspace

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class WorkspaceLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.WORKSPACE,
        key = "'workspaceId:' + {#workspaceId}",
        targetCacheStrategies = [CacheStrategy.LOCAL],
    )
    suspend fun evict(
        workspaceId: String,
    ) {
    }

}
