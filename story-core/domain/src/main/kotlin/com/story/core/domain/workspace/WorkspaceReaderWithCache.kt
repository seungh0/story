package com.story.core.domain.workspace

import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class WorkspaceReaderWithCache(
    private val workspaceReader: WorkspaceReader,
) {

    @Cacheable(
        cacheType = CacheType.WORKSPACE,
        key = "'workspaceId:' + {#workspaceId}",
    )
    suspend fun getWorkspace(
        workspaceId: String,
    ): Optional<Workspace> {
        val workspace = workspaceReader.getWorkspace(workspaceId)
            ?: return Optional.empty()
        return Optional.of(workspace)
    }

}
