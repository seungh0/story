package com.story.core.domain.purge

import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequest
import com.story.core.domain.component.ComponentRetriever
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.workspace.WorkspaceArchiveRepository
import com.story.core.domain.workspace.WorkspaceNotExistsException
import com.story.core.infrastructure.cassandra.executeCoroutine
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class WorkspacePurger(
    private val componentRetriever: ComponentRetriever,
    private val purgeManager: PurgeManager,
    private val reactiveCassandraTemplate: ReactiveCassandraTemplate,
    private val workspaceArchiveRepository: WorkspaceArchiveRepository,
) {

    suspend fun cleanWorkspace(workspaceId: String) {
        val workspaceArchive = workspaceArchiveRepository.findById(workspaceId)
            ?: throw WorkspaceNotExistsException("아카이빙된 워크스페이스($workspaceId)가 존재하지 않습니다")

        if (!workspaceArchive.canPurge(LocalDateTime.now())) {
            throw WorkspacePurgeRetentionPeriodViolationException("삭제 전 최소 보관 기간이 지나지 않은 워크스페이스($workspaceId)입니다")
        }

        ResourceId.values().forEach { resourceId ->
            var cursor = CursorRequest.first(direction = CursorDirection.NEXT, pageSize = 50)
            do {
                val components = componentRetriever.listComponents(
                    workspaceId = workspaceArchive.workspaceId,
                    resourceId = resourceId,
                    cursorRequest = cursor,
                )
                components.data.forEach { component ->
                    purgeManager.publishEvent(
                        resourceId,
                        workspaceId = workspaceArchive.workspaceId,
                        componentId = component.componentId
                    )
                }
                cursor = cursor.copy(cursor = components.cursor.nextCursor)
            } while (components.hasNext)
        }

        reactiveCassandraTemplate.batchOps()
            .delete(workspaceArchive)
            .delete(workspaceArchive.toWorkspace())
            .executeCoroutine()
    }

}
