package com.story.core.domain.workspace

import com.story.core.infrastructure.cassandra.executeCoroutine
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Repository

@Repository
class WorkspaceArchiveEntityRepository(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val workspaceArchiveRepository: WorkspaceArchiveRepository,
) : WorkspaceArchiveWriteRepository, WorkspaceArchiveReadRepository {

    override suspend fun delete(workspaceArchive: WorkspaceArchive) {
        val entity = WorkspaceArchiveEntity.from(workspaceArchive)
        reactiveCassandraOperations.batchOps()
            .delete(entity)
            .delete(entity.toWorkspaceEntity())
            .executeCoroutine()
    }

    override suspend fun findById(workspaceId: String): WorkspaceArchive? {
        return workspaceArchiveRepository.findById(workspaceId)?.toWorkspaceArchive()
    }

}
