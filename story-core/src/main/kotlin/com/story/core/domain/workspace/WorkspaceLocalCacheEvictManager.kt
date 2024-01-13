package com.story.core.domain.workspace

import com.story.core.common.logger.LoggerExtension.log
import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
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
        log.info { "Workspace 로컬 캐시가 만료됩니다 [workspaceId: $workspaceId]" }
    }

}