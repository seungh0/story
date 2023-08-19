package com.story.platform.core.domain.workspace

import com.story.platform.core.common.logger.LoggerExtension.log
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheStrategyType
import com.story.platform.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class WorkspaceLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.WORKSPACE,
        key = "'workspaceId:' + {#workspaceId}",
        targetCacheStrategies = [CacheStrategyType.LOCAL],
    )
    suspend fun evict(
        workspaceId: String,
    ) {
        log.info { "Workspace 로컬 캐시가 만료됩니다 [workspaceId: $workspaceId]" }
    }

}
