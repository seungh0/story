package com.story.platform.core.domain.purge

import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.component.ComponentRetriever
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.workspace.WorkspaceNotExistsException
import com.story.platform.core.domain.workspace.WorkspaceRepository
import com.story.platform.core.infrastructure.cassandra.expire
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class WorkspacePurger(
    private val workspaceRepository: WorkspaceRepository,
    private val componentRetriever: ComponentRetriever,
    private val purgeManager: PurgeManager,
    private val reactiveCassandraTemplate: ReactiveCassandraTemplate,
) {

    suspend fun cleanWorkspace(workspaceId: String) {
        val workspace = workspaceRepository.findById(workspaceId)
            ?: throw WorkspaceNotExistsException("워크스페이스($workspaceId)는 존재하지 않습니다")

        ResourceId.values().forEach { resourceId ->
            var cursor = CursorRequest.first(direction = CursorDirection.NEXT, pageSize = 50)
            do {
                val components = componentRetriever.listComponents(
                    workspaceId = workspace.workspaceId,
                    resourceId = resourceId,
                    cursorRequest = cursor,
                )
                components.data.forEach { component ->
                    purgeManager.publishEvent(
                        resourceId,
                        workspaceId = workspace.workspaceId,
                        componentId = component.componentId
                    )
                }
                cursor = cursor.copy(cursor = components.cursor.nextCursor)
            } while (components.hasNext())
        }

        reactiveCassandraTemplate.expire(workspace, Duration.ofDays(30)).awaitSingle()
    }

}
