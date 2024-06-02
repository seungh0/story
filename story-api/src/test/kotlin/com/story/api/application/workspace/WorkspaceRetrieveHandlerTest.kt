package com.story.api.application.workspace

import com.story.core.domain.workspace.WorkspaceEntityFixture
import com.story.core.domain.workspace.WorkspaceNotExistsException
import com.story.core.domain.workspace.WorkspaceReaderWithCache
import com.story.core.domain.workspace.WorkspaceStatus
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import java.util.Optional

class WorkspaceRetrieveHandlerTest : StringSpec({

    val workspaceReaderWithCache = mockk<WorkspaceReaderWithCache>()
    val workspaceRetrieverHandler = WorkspaceRetrieveHandler(
        workspaceReaderWithCache = workspaceReaderWithCache,
    )

    "존재하지 않는 워크스페이스인 경우 throws NotExistsWorkspaceException" {
        // given
        val workspace = WorkspaceEntityFixture.create(status = WorkspaceStatus.ENABLED)
        coEvery { workspaceReaderWithCache.getWorkspace(any()) } returns Optional.of(workspace.toWorkspace())

        // when & then
        shouldNotThrowAny {
            workspaceRetrieverHandler.validateEnabledWorkspace(workspaceId = workspace.workspaceId)
        }
    }

    "삭제된 워크스페이스인 경우 throws NotExistsWorkspaceException" {
        // given
        val workspace = WorkspaceEntityFixture.create(status = WorkspaceStatus.DELETED)
        coEvery { workspaceReaderWithCache.getWorkspace(any()) } returns Optional.of(workspace.toWorkspace())

        // when & then
        shouldThrowExactly<WorkspaceNotExistsException> {
            workspaceRetrieverHandler.validateEnabledWorkspace(workspaceId = workspace.workspaceId)
        }
    }

})
