package com.story.core.domain.workspace

import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import org.springframework.stereotype.Service

@Service
class WorkspaceRemover(
    private val workspaceWriteRepository: WorkspaceWriteRepository,
) {

    @CacheEvict(
        cacheType = CacheType.WORKSPACE,
        key = "'workspaceId:' + {#workspaceId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL],
    )
    suspend fun removeWorkspace(
        workspaceId: String,
    ) {
        workspaceWriteRepository.delete(workspaceId)
    }

}
