package com.story.core.domain.workspace

import com.story.core.support.cassandra.executeCoroutine
import com.story.core.support.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Repository

@Repository
class WorkspaceEntityRepository(
    private val workspaceCassandraRepository: WorkspaceCassandraRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) : WorkspaceWriteRepository, WorkspaceReadRepository {

    override suspend fun create(workspaceId: String, name: String, plan: WorkspacePricePlan): Workspace {
        if (workspaceCassandraRepository.existsById(workspaceId)) {
            throw WorkspaceAlreadyExistsException("워크스페이스($workspaceId)는 이미 존재합니다")
        }

        val workspace = WorkspaceEntity.of(
            workspaceId = workspaceId,
            name = name,
            plan = plan,
        )

        return workspaceCassandraRepository.save(workspace).toWorkspace()
    }

    override suspend fun delete(workspaceId: String) {
        val workspace = workspaceCassandraRepository.findById(workspaceId)
            ?: throw WorkspaceNotExistsException(message = "워크스페이스($workspaceId)가 존재하지 않습니다")
        workspace.delete()
        reactiveCassandraOperations.batchOps()
            .upsert(workspace)
            .upsert(WorkspaceArchiveEntity.from(workspace))
            .executeCoroutine()
    }

    override suspend fun findById(workspaceId: String): Workspace? {
        return workspaceCassandraRepository.findById(workspaceId)?.toWorkspace()
    }

}
