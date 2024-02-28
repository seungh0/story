package com.story.core.domain.workspace

import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class WorkspaceRetriever(
    private val workspaceRepository: WorkspaceRepository,
) {

    @Cacheable(
        cacheType = CacheType.WORKSPACE,
        key = "'workspaceId:' + {#workspaceId}",
    )
    suspend fun getWorkspace(
        workspaceId: String,
    ): Optional<WorkspaceResponse> {
        val workspace = workspaceRepository.findById(workspaceId)
            ?: return Optional.empty()
        return Optional.of(WorkspaceResponse.of(workspace = workspace))
    }

}
