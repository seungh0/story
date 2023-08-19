package com.story.platform.core.domain.workspace

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheStrategyType
import com.story.platform.core.support.cache.CacheType
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
        targetCacheStrategies = [CacheStrategyType.GLOBAL],
    )
    suspend fun remove(
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
