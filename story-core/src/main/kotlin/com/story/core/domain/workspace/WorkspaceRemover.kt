package com.story.core.domain.workspace

import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class WorkspaceRemover(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val workspaceRepository: WorkspaceRepository,
) {

    @CacheEvict(
        cacheType = CacheType.WORKSPACE,
        key = "'workspaceId:' + {#workspaceId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL],
    )
    suspend fun removeWorkspace(
        workspaceId: String,
    ) {
        val workspace = workspaceRepository.findById(workspaceId)
            ?: throw WorkspaceNotExistsException(message = "워크스페이스($workspaceId)가 존재하지 않습니다")
        workspace.delete()
        reactiveCassandraOperations.batchOps()
            .upsert(workspace)
            .upsert(WorkspaceArchive.from(workspace))
            .executeCoroutine()
    }

}
