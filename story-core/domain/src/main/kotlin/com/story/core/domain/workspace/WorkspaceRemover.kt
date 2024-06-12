package com.story.core.domain.workspace

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
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
